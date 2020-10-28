package handson;

import com.commercetools.api.client.ApiRoot;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static handson.impl.ClientService.*;


/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task02a_CREATE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // TODO:
        //  Provide your Api client prefix
        //
        String apiClientPrefix = "mh-dev-admin.";

        Logger logger = LoggerFactory.getLogger(Task02a_CREATE.class.getName());
        final ApiRoot client = createApiClient(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, getProjectKey(apiClientPrefix));

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {

            logger.info("Customer fetch: " +
                    customerService
                            .getCustomerByKey("customer-alex-242281870")
                            .toCompletableFuture().get()
                            .getBody().getEmail()
            );

            // TODO:
            //  CREATE a customer
            //  CREATE a email verification token
            //  Verify customer
            //
            logger.info("Customer created: " +
                    customerService.createCustomer(
                            "michael15@example.com",
                            "password",
                            "customer-michael15",
                            "michael",
                            "hartwig",
                            "DE"
                    )
                            .thenComposeAsync(signInResult -> customerService.createEmailVerificationToken(signInResult, 5))
                            .thenComposeAsync(customerService::verifyEmail)
                            .toCompletableFuture().get()
                            .getBody().toPrettyString()
            );
        }

    }
}
