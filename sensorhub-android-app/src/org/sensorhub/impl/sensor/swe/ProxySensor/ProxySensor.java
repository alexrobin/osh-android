package org.sensorhub.impl.sensor.swe.ProxySensor;

import android.util.Log;

import org.sensorhub.api.common.SensorHubException;
import org.sensorhub.impl.sensor.swe.SWEVirtualSensor;

import java.util.ArrayList;
import java.util.List;

import net.opengis.sensorml.v20.AbstractPhysicalProcess;
import net.opengis.swe.v20.DataComponent;

import org.sensorhub.impl.client.sos.SOSClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.vast.ows.GetCapabilitiesRequest;
import org.vast.ows.OWSException;
import org.vast.ows.OWSUtils;
import org.vast.ows.sos.GetResultRequest;
import org.vast.ows.sos.SOSOfferingCapabilities;
import org.vast.ows.sos.SOSServiceCapabilities;
import org.vast.ows.sos.SOSUtils;
import org.vast.util.TimeExtent;

public class ProxySensor extends SWEVirtualSensor {
    //protected static final Logger log = LoggerFactory.getLogger(ProxySensor.class);
    private static final String TAG = "OSHProxySensor";
    private static final String SOS_VERSION = "2.0";
    private static final String SPS_VERSION = "2.0";
    private static final double STREAM_END_TIME = 2e9; //

    List<SOSClient> sosClients;

    public ProxySensor() {
        super();
    }

    @Override
    public void start() throws SensorHubException {
        //log.debug("Starting Proxy Sensor");
        Log.d(TAG, "Starting Proxy Sensor");

        checkConfig();
        removeAllOutputs();
        removeAllControlInputs();
        OWSUtils owsUtils = new OWSUtils();

        if (config.sosEndpointUrl != null)
        {
            SOSServiceCapabilities sosServiceCapabilities;
            try
            {
                GetCapabilitiesRequest getCapabilitiesRequest = new GetCapabilitiesRequest();
                getCapabilitiesRequest.setService(SOSUtils.SOS);
                getCapabilitiesRequest.setVersion(SOS_VERSION);
                getCapabilitiesRequest.setGetServer(config.sosEndpointUrl);
                sosServiceCapabilities = owsUtils.sendRequest(getCapabilitiesRequest, false);
            }
            catch (OWSException e)
            {
                throw new SensorHubException("Cannot retrieve SOS capabilities", e);
            }

            // Scan all offering and connect to selected ones
            sosClients = new ArrayList<>(config.observedProperties.size());
            for (SOSOfferingCapabilities sosOffering: sosServiceCapabilities.getLayers())
            {
                int offeringIndex = sosServiceCapabilities.getLayers().indexOf(sosOffering);

                if (sosOffering.getMainProcedure().equals(config.sensorUID))
                {
                    for (String observableProperty: config.observedProperties)
                    {
                        if (sosOffering.getObservableProperties().contains(observableProperty))
                        {
                            // Build GetResult request
                            GetResultRequest getResultRequest = new GetResultRequest();
                            getResultRequest.setGetServer(config.sosEndpointUrl);
                            getResultRequest.setVersion(SOS_VERSION);
                            getResultRequest.setOffering(sosOffering.getIdentifier());
                            getResultRequest.getObservables().add(observableProperty);
                            getResultRequest.setTime(TimeExtent.getPeriodStartingNow(STREAM_END_TIME));
                            getResultRequest.setXmlWrapper(false);

                            // Create client for GetResult
                            SOSClient sosClient = new SOSClient(getResultRequest, config.sosUseWebsockets);
                            sosClients.add(sosClient);

                            // Request result's template
                            sosClient.retrieveStreamDescription();
                            DataComponent dataComponent = sosClient.getRecordDescription();
                            if (dataComponent.getName() == null)
                            {
                                dataComponent.setName("output_" + (offeringIndex + 1));
                            }

                            // Get sensor description if available (first time only)
                            try
                            {
                                if (offeringIndex == 0 && config.sensorML == null)
                                {
                                    this.sensorDescription = (AbstractPhysicalProcess) sosClient.getSensorDescription(config.sensorUID);
                                }
                            }
                            catch (SensorHubException e)
                            {
                                //log.warn("Cannot get remote sensor description", e);
                                Log.w(TAG, "Cannot get remote sensor description");
                            }

                            // Create output
                            final ProxySensorOutput proxySensorOutput = new ProxySensorOutput(this, dataComponent, sosClient.getRecommendedEncoding());
                            this.addOutput(proxySensorOutput, false);

                            sosClient.startStream(data -> proxySensorOutput.publishNewRecord(data));
                        }
                    }
                }
            }

            if (sosClients.isEmpty())
            {
                throw new SensorHubException("Requested observation data is not available from SOS " + config.sosEndpointUrl +
                        ". Check Sensor UID and observed properties have valid values." );
            }
        }
    }
}
