package handson;

import com.commercetools.api.client.ApiRoot;
import handson.impl.ClientService;
import handson.impl.CustomerService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static handson.impl.ClientService.createApiClient;


/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task02a_CREATE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = Logger.getLogger(Task02a_CREATE.class.getName());
        final ApiRoot client = createApiClient("mh-dev-admin.");
        CustomerService customerService = new CustomerService(client, "barbara-merchant-center");

//        logger.log(Level.INFO, "Customer fetch: " +
//                customerService
//                        .getCustomerByKey("customer-michele")
//                        .toCompletableFuture().get()
//                        .getBody().getEmail()
//        );

            // TODO:
            //  CREATE a customer
            //  CREATE a email verification token
            //  Verify customer
            //
        logger.log(Level.INFO, "Customer created: " +
                            customerService.createCustomer(
                                    "michael12@example.com",
                                    "password",
                                    "customer-michael12",
                                    "michael",
                                    "hartwig",
                                    "DE"
                            )
                            .thenComposeAsync(signInResult -> customerService.createEmailVerificationToken(signInResult.getBody().getCustomer(), 15))
                            .thenComposeAsync(customerTokenApiHttpResponse -> customerService.verifyEmail(customerTokenApiHttpResponse.getBody()))
                            .toCompletableFuture().get()
                            .getBody()
        );

    }
}
