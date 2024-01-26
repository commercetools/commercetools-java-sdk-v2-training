package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerBuilder;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger(Task02a_CREATE.class.getName());
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        CustomerService customerService = new CustomerService(client);

//        logger.info("Customer fetch: " +
//                customerService
//                        .getCustomerByKey("customer-alex-242281870")
//                        .get()
//                        .getBody().getEmail()
//        );

        // TODO:
        //  CREATE a customer
        //  CREATE a email verification token
        //  Verify customer
        //

        customerService.createCustomer(
                "michael16@example.com",
                "password",
                "customer-michael16",
                "michael",
                "tester",
                "DE"
        )
        .thenComposeAsync(signInResult -> customerService.createEmailVerificationToken(signInResult, 5))
        .thenComposeAsync(customerService::verifyEmail)
                .thenApply(ApiHttpResponse::getBody)
                .thenAccept(resource -> logger.info("Resource ID: " + resource.getId()))
                .exceptionally(exception -> { logger.info("An error occured " + exception.getMessage()); return null;})
                .thenRun(() -> client.close());
    }
}
