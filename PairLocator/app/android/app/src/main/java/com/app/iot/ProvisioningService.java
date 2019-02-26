package com.app.iot;

import com.microsoft.azure.sdk.iot.deps.util.Base64;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.provisioning.device.*;
import com.microsoft.azure.sdk.iot.provisioning.device.internal.exceptions.ProvisioningDeviceClientException;
import com.microsoft.azure.sdk.iot.provisioning.security.hsm.SecurityProviderX509Cert;
import com.microsoft.azure.sdk.iot.provisioning.security.exceptions.SecurityProviderException;

import java.io.IOException;
import java.util.Scanner;

//How to generate self-signed certificate: https://docs.microsoft.com/en-us/azure/iot-dps/quick-create-simulated-device-x509-java
public class ProvisioningService 
{
    private final String idScope = "0ne000477BF";
    private final String globalEndpoint = "global.azure-devices-provisioning.net";
    private final ProvisioningDeviceClientTransportProtocol PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL = ProvisioningDeviceClientTransportProtocol.HTTPS;
    private final int MAX_TIME_TO_WAIT_FOR_REGISTRATION = 10000;
    private final String leafPublicPem = "-----BEGIN CERTIFICATE-----\n" +
        "MIICKzCCAdCgAwIBAgIFCgsMDQ4wCgYIKoZIzj0EAwIwQjEgMB4GA1UEAwwXbWlj\n" +
        "cm9zb2Z0cmlvdGNvcmVzaWduZXIxETAPBgNVBAoMCE1TUl9URVNUMQswCQYDVQQG\n" +
        "EwJVUzAgFw0xNzAxMDEwMDAwMDBaGA8zNzAxMDEzMTIzNTk1OVowPDEaMBgGA1UE\n" +
        "AwwRbWljcm9zb2Z0cmlvdGNvcmUxETAPBgNVBAoMCE1TUl9URVNUMQswCQYDVQQG\n" +
        "EwJVUzBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABLMwFV6wky1oepS5jTL/C5uC\n" +
        "OerpxnsH6yN2TaKJFDe0aTLBWQ1d9Zp5u4A4ua29DJi1S9paPOn5QNpCuWi3w4yj\n" +
        "gbYwgbMwEwYDVR0lBAwwCgYIKwYBBQUHAwIwgZsGBmeBBQUEAQSBkDCBjQIBATBZ\n" +
        "MBMGByqGSM49AgEGCCqGSM49AwEHA0IABBdP1mVtuycFqVbsMg7IoKdOl245dmLv\n" +
        "M+GbOr+q56FGDuqr5fP755lTfVTWsA2lwaziWIaDrtysHd6UTSE4jgAwLQYJYIZI\n" +
        "AWUDBAIBBCAREhMUBQYHCAECAwQFBgcIAQIDBAUGBwgBAgMEBQYHCDAKBggqhkjO\n" +
        "PQQDAgNJADBGAiEA6GN4peNOO7seCZctbWzD1nf8i3414zIGbKK8PDmUXH4CIQCQ\n" +
        "dRi2dn3bQatk4VvWrYY/6QC1WltZHXJobieuaMWayw==\n" +
        "-----END CERTIFICATE-----\n";
    private final String leafPrivateKey = "-----BEGIN PRIVATE KEY-----\n" +
        "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgi+wLJqCQ0xTkUMTU\n" +
        "ZWr4GvTitfeq5AIL6IDdNCvSeFygCgYIKoZIzj0DAQehRANCAASzMBVesJMtaHqU\n" +
        "uY0y/wubgjnq6cZ7B+sjdk2iiRQ3tGkywVkNXfWaebuAOLmtvQyYtUvaWjzp+UDa\n" +
        "Qrlot8OM\n" +
        "-----END PRIVATE KEY-----\n";
    private final Collection<String> signerCertificates = new LinkedList<>();

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
            SecurityProvider securityProviderX509 = new SecurityProviderX509Cert(leafPublicPem, leafPrivateKey, signerCertificates);
            provisioningDeviceClient = ProvisioningDeviceClient.create(globalEndpoint, idScope, PROVISIONING_DEVICE_CLIENT_TRANSPORT_PROTOCOL,
                                                                       securityProviderX509);

            provisioningDeviceClient.registerDevice(new ProvisioningDeviceClientRegistrationCallbackImpl(), provisioningStatus);

            while (provisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() != ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_ASSIGNED)
            {
                if (provisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() == ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_ERROR ||
                        provisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() == ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_DISABLED ||
                        provisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() == ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_FAILED )

                {
                    break;
                }

                Thread.sleep(MAX_TIME_TO_WAIT_FOR_REGISTRATION);
            }

            if (provisioningStatus.provisioningDeviceClientRegistrationInfoClient.getProvisioningDeviceClientStatus() == ProvisioningDeviceClientStatus.PROVISIONING_DEVICE_STATUS_ASSIGNED)
            {
                String iotHubUri = provisioningStatus.provisioningDeviceClientRegistrationInfoClient.getIothubUri();
                String deviceId = provisioningStatus.provisioningDeviceClientRegistrationInfoClient.getDeviceId();
            }

            try
            {
                deviceClient = DeviceClient.createFromSecurityProvider(iotHubUri, deviceId, securityProviderX509, IotHubClientProtocol.MQTT);
                deviceClient.open();
                Message messageToSendFromDeviceToHub =  new Message("Whatever message you would like to send");

                deviceClient.sendEventAsync(messageToSendFromDeviceToHub, new IotHubEventCallbackImpl(), null);
            }
            catch (IOException e)
            {
                if (deviceClient != null)
                {
                    deviceClient.closeNow();
                }
            }
        } 
        catch (ProvisioningDeviceClientException | InterruptedException e) 
        {
            if (provisioningDeviceClient != null)
            {
                provisioningDeviceClient.closeNow();
            }
        }

        if (provisioningDeviceClient != null)
        {
            provisioningDeviceClient.closeNow();
        }
        if (deviceClient != null)
        {
            deviceClient.closeNow();
        }
    }
}