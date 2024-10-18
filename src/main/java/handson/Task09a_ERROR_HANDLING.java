package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer.Customer;
import handson.impl.ApiPrefixHelper;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task09a_ERROR_HANDLING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot apiRoot = createApiClient(apiClientPrefix);

        final String storeKey = getStoreKey(apiClientPrefix);
        CustomerService customerService = new CustomerService(apiRoot, storeKey);

        // TODO:
        //  Provide a WRONG or CORRECT customer key
        //
        final String customerKeyMayOrMayNotExist = "customer-michele-WRONG-KEY";

        // TODO: Handle 4XX errors, exceptions
        //  Use CompletionStage
        //

        customerService
            .getCustomerByKey(customerKeyMayOrMayNotExist)
            .thenApply(ApiHttpResponse::getBody) // unpack response body
            .thenAccept(customer -> logger.info("Customer fetch: " + customer.get().getEmail()))
            .exceptionally(throwable -> {
                logger.info("Customer " + customerKeyMayOrMayNotExist + " does not exist.");
                // handle it
                return null; // e.g. return anon customer
            });


        // TODO: Handle 4XX errors, exceptions
        //  Use Optionals, Either (Java 9+)
        //
        Optional<Customer> optionalCustomer = Optional.ofNullable(
            customerService
                .getCustomerByKey("customer-michele-WRONG-KEY")
                .thenApply(ApiHttpResponse::getBody)
                .exceptionally(throwable -> null)
                .get()
        );

        if (!optionalCustomer.isPresent()) {
            logger.info("Customer " + customerKeyMayOrMayNotExist + " does not exist.");
            // handle it, return anon customer, etc.
        }

        optionalCustomer.ifPresent(customer -> {
            logger.info("Customer: " + customerKeyMayOrMayNotExist + "exists.");
            try {
                customerService.createEmailVerificationToken(customer, 5)
                    .thenComposeAsync(customerTokenApiHttpResponse -> customerService.verifyEmail(
                            customerTokenApiHttpResponse.getBody()
                    ))
                    .get();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
