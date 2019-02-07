package com.app.iot;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class IoTModule extends ReactContextBaseJavaModule {
    public IoTModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "IoT";
    }

    @ReactMethod
    public void getMessage(Callback cb) {
       try{
           cb.invoke(null, "hello world");
       }catch (Exception e){
           cb.invoke(e.toString(), null);
       }
   }
}