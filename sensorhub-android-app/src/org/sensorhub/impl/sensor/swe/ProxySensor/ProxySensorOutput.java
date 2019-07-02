package org.sensorhub.impl.sensor.swe.ProxySensor;

import android.util.Log;

import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataEncoding;

import org.sensorhub.api.common.IEventListener;
import org.sensorhub.impl.sensor.swe.SWEVirtualSensor;
import org.sensorhub.impl.sensor.swe.SWEVirtualSensorOutput;

import static android.content.ContentValues.TAG;

public class ProxySensorOutput extends SWEVirtualSensorOutput
{
    private boolean isListening = false;

    public ProxySensorOutput(SWEVirtualSensor sensor, DataComponent recordStructure, DataEncoding recordEncoding) {
        super(sensor, recordStructure, recordEncoding);
    }

    @Override
    public void publishNewRecord(DataBlock dataBlock) {
        Log.d(TAG, "publishNewRecord");

        super.publishNewRecord(dataBlock);
    }

    @Override
    public void registerListener(IEventListener listener) {
        Log.d(TAG, "registerListener");

        if (!isListening) {
            super.registerListener(listener);
            isListening = true;
        }
    }

    /*
    public void registerListener(IEventListener listener)
    {
        Log.d(TAG, "Registering Proxy Sensor Listener");

        //TODO: How to start the SOS stream at this point?
        // task sos client to output. 1 sos client per output.

        //try {
        //    this.parentSensor.startSOSStreams();
        //} catch (SensorHubException e) {
        //    Log.d(TAG, "Error Starting Stream while registering Proxy Sensor", e);
        //}
        //eventHandler.registerListener(listener);
    }
    */
}
