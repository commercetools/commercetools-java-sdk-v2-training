package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


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

        Logger logger = LoggerFactory.getLogger("commercetools");
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        final String storeKey = getStoreKey(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, storeKey);

        customerService
                .getCustomerByKey("customer-michael")
                .thenApply(ApiHttpResponse::getBody)
                .handle((customer, exception) -> {
                    if (exception == null) {
                        logger.info("Customer already exists: " + customer.getEmail()); return customer;
                    };
                    logger.error("Exception: " + exception.getMessage());
                    return null;
                });

        // TODO:
        //  CREATE a customer
        //  CREATE a email verification token
        //  Verify customer
        //

        customerService.createCustomer(
                "tester@example.com",
                "password",
                "customer-tester",
                "tester",
                "last",
                "DE"
        )
        .thenComposeAsync(signInResult -> customerService.createEmailVerificationToken(signInResult, 5))
        .thenComposeAsync(customerService::verifyEmail)
                .thenApply(ApiHttpResponse::getBody)
                .handle((customer, exception) -> {
                    if (exception == null) {
                        logger.info("Customer ID: " + customer.getId()); return customer;
                    };
                    logger.error("Exception: " + exception.getMessage());
                    return null;
                }).thenRun(() -> client.close());
    }
}
