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
        CustomerService customerService = new CustomerService(client);

        customerService
                .getCustomerByKey("customer-michael")
                .thenApply(ApiHttpResponse::getBody)
                .handle((customer, exception) -> {
                    if (exception != null) {
                        logger.error("Exception: " + exception.getMessage());
                        return null;
                    };
                    logger.info("Customer already exists: " + customer.getEmail()); return customer;});

        // TODO:
        //  CREATE a customer
        //  CREATE a email verification token
        //  Verify customer
        //

        customerService.createCustomer(
                "michael15@example.com",
                "password",
                "customer-michael15",
                "michael15",
                "tester",
                "DE"
        )
        .thenComposeAsync(signInResult -> customerService.createEmailVerificationToken(signInResult, 5))
        .thenComposeAsync(customerService::verifyEmail)
                .thenApply(ApiHttpResponse::getBody)
                .handle((customer, exception) -> {
                    if (exception != null) {
                        logger.error("Exception: " + exception.getMessage());
                        return null;
                    };
                    logger.info("Resource ID: " + customer.getId()); return customer;})
                .thenRun(() -> client.close());
    }
}
