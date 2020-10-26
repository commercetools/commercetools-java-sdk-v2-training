package handson;


import com.commercetools.api.client.ApiRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

public class Task09b_SPHERECLIENT_LOGGING {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey("mh-dev-admin.");
        final ApiRoot client = createApiClient("mh-dev-admin.");
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

            // TODO

            // 1
            // Intensive logging, inspect returning headers, log all calls

            // 2
            // ReUse tokens

            // 3
            // Arrays-asList for UpdateActions

            // 4
            // Decorate client for X-Correlation-ID

            // 5
            // Simulate failover, 5xx errors

    }
}
