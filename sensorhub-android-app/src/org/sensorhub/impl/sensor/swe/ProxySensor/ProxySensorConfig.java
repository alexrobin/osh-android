package org.sensorhub.impl.sensor.swe.ProxySensor;

import org.sensorhub.impl.sensor.swe.SWEVirtualSensorConfig;

public class ProxySensorConfig extends SWEVirtualSensorConfig
{
    public ProxySensorConfig()
    {
        super();

        this.moduleClass = ProxySensor.class.getCanonicalName();
    }
}
