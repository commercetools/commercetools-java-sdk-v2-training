package handson;

import com.commercetools.api.client.ApiRoot;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task02a_CREATE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(Task02a_CREATE.class.getName());
        final ApiRoot client = createApiClient("mh-dev-admin.");
        CustomerService customerService = new CustomerService(client, getProjectKey("mh-dev-admin."));

        logger.info("Customer fetch: " +
                customerService
                        .getCustomerByKey("customer-michele")
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
                                    "michael12@example.com",
                                    "password",
                                    "customer-michael12",
                                    "michael",
                                    "hartwig",
                                    "DE"
                            )
                            .thenComposeAsync(signInResult -> customerService.createEmailVerificationToken(signInResult.getBody().getCustomer(), 5))
                            .thenComposeAsync(customerTokenApiHttpResponse -> customerService.verifyEmail(customerTokenApiHttpResponse.getBody()))
                            .toCompletableFuture().get()
                            .getBody()
        );

    }
}
