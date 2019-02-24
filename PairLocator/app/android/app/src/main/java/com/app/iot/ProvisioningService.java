package com.app.iot;

import com.microsoft.azure.sdk.iot.deps.util.Base64;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.provisioning.device.*;
import com.microsoft.azure.sdk.iot.provisioning.device.internal.exceptions.ProvisioningDeviceClientException;
import com.microsoft.azure.sdk.iot.provisioning.security.hsm.SecurityProviderX509Cert;
import com.microsoft.azure.sdk.iot.provisioning.security.exceptions.SecurityProviderException;

import java.io.IOException;
import java.util.Scanner;

public class ProvisioningService 
{
    private final String idScope = "0ne000477BF";
    private final String globalEndpoint = "global.azure-devices-provisioning.net";
    private final ProvisioningDeviceClientTransportProtocol PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL = ProvisioningDeviceClientTransportProtocol.HTTPS;
    private final int MAX_TIME_TO_WAIT_FOR_REGISTRATION = 10000;
    private final String leafPublicPem = "<Your Public Leaf Certificate Here>";
    private final String leafPrivateKey = "<Your Leaf Key Here>";

    class ProvisioningStatus
    {
        ProvisioningDeviceClientRegistrationResult provisioningDeviceClientRegistrationInfoClient = new ProvisioningDeviceClientRegistrationResult();
        Exception exception;
    }

    class ProvisioningDeviceClientRegistrationCallbackImpl implements ProvisioningDeviceClientRegistrationCallback
    {
        @Override
        public void run(ProvisioningDeviceClientRegistrationResult provisioningDeviceClientRegistrationResult, Exception exception, Object context)
        {
            if (context instanceof ProvisioningStatus)
            {
                ProvisioningStatus status = (ProvisioningStatus) context;
                status.provisioningDeviceClientRegistrationInfoClient = provisioningDeviceClientRegistrationResult;
                status.exception = exception;
            }
            else
            {
                System.out.println("Received unknown context");
            }
        }
    }

    private class IotHubEventCallbackImpl implements IotHubEventCallback
    {
        @Override
        public void execute(IotHubStatusCode responseStatus, Object callbackContext)
        {
            System.out.println("Message received!");
        }
    }

    public void startProvisioning()
    {
        ProvisioningDeviceClient provisioningDeviceClient = null;
        DeviceClient deviceClient = null;

        try 
        {
            ProvisioningStatus provisioningStatus = new ProvisioningStatus();
        } 
        catch (ProvisioningDeviceClientException | InterruptedException e) 
        {
            if (provisioningDeviceClient != null)
            {
                provisioningDeviceClient.closeNow();
            }
        }
    }
}