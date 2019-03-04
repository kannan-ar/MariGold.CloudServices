package com.app.iot;

import com.microsoft.azure.sdk.iot.device.*;

import com.google.gson.Gson;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;



public class IoTModule extends ReactContextBaseJavaModule {
    
    private class TelemetryDataPoint {
        public double lat;
        public double lng;
    
        // Serialize object to JSON format.
        public String serialize() {
          Gson gson = new Gson();
          return gson.toJson(this);
        }
    }
    
    private class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            if (context != null) {
                synchronized (context) {
                    context.notify();
                }
            }
        }
    }

    private String connString = "HostName=pair-mob-devices.azure-devices.net;DeviceId=MyPythonDevice;SharedAccessKey=RqbWt8V2QCMV04rSBwx1FhZ+a9Gyt2wCdxBWGHtC4Ek=";
    private IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;

    public IoTModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "IoT";
    }

    @ReactMethod
    public void pingIoT(double lat, double lng, Callback cb) throws IOException, URISyntaxException, InterruptedException {
        try{
            ProvisioningService provisioningService = new ProvisioningService();
            provisioningService.startProvisioning();

            /*DeviceClient client = new DeviceClient(connString, protocol);
            client.open();
            TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
            telemetryDataPoint.lat = lat;
            telemetryDataPoint.lng = lng;

            
            String msgStr = telemetryDataPoint.serialize();
            Message msg = new Message(msgStr);

            Object lockobj = new Object();

            EventCallback callback = new EventCallback();
            client.sendEventAsync(msg, callback, lockobj);

            synchronized (lockobj) {
                lockobj.wait();
            }*/
            
            cb.invoke(null, "test hello");
        }catch (Exception e){
            cb.invoke(e.toString(), e.toString());
        }

        //client.closeNow();
   }
}