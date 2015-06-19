/***************************** BEGIN LICENSE BLOCK ***************************

The contents of this file are subject to the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file, You can obtain one
at http://mozilla.org/MPL/2.0/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the License.
 
Copyright (C) 2012-2015 Sensia Software LLC. All Rights Reserved.
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorhub.android.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.sensorhub.api.comm.CommConfig;
import org.sensorhub.api.comm.ICommProvider;
import org.sensorhub.impl.comm.BluetoothConfig;
import android.bluetooth.BluetoothSocket;


/**
 * <p>
 * Communication provider for Bluetooth Sockets (i.e. Serial Port Profile)
 * using the Android API
 * </p>
 *
 * @author Alex Robin <alex.robin@sensiasoftware.com>
 * @since Jun 18, 2015
 */
public class BluetoothCommProvider implements ICommProvider
{
    BluetoothSocket btSocket;
    
    
    public BluetoothCommProvider() 
    {
    }
    
    
    public void init(CommConfig config) throws IOException
    {
        BluetoothConfig btConf = (BluetoothConfig)config;
        BluetoothManager btManager = new BluetoothManager();
        btSocket = btManager.connectToSerialDevice(btConf.deviceName);
    }
    
    
    @Override
    public InputStream getInputStream() throws IOException
    {
        return btSocket.getInputStream();
    }


    @Override
    public OutputStream getOutputStream() throws IOException
    {
        return btSocket.getOutputStream();
    }


    @Override
    public void close()
    {
        try
        {
            btSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }        
    }

}
