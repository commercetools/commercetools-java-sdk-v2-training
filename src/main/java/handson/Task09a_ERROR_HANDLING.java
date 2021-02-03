package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerBuilder;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import handson.impl.PrefixHelper;
import io.vrap.rmf.base.client.ApiHttpClient;
import io.vrap.rmf.base.client.ApiHttpResponse;
import io.vrap.rmf.base.client.error.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task09a_ERROR_HANDLING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = PrefixHelper.getDevApiClientPrefix();

        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task09a_ERROR_HANDLING.class.getName());

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            CustomerService customerService = new CustomerService(client, projectKey);

            // TODO:
            //  Provide a WRONG or CORRECT customer key
            //
            final String customerKeyMayOrMayNotExist = "customer-michele-WRONG-KEY";

            // TODO: Handle 4XX errors, exceptions
            //  Use CompletionStage
            //
            logger.info("Customer fetch: " +
                    customerService
                            .getCustomerByKey(customerKeyMayOrMayNotExist)
                            .thenApply(ApiHttpResponse::getBody) // unpack response body
                            .exceptionally(throwable -> {
                                logger.info("Customer " + customerKeyMayOrMayNotExist + " does not exist.");
                                // handle it
                                return
                                        CustomerBuilder.of()
                                                .email("anonymous@example.org")
                                                .build();                               // e.g. return anon customer
                            })
                            .toCompletableFuture().get().getEmail()
            );


            // TODO: Handle 4XX errors, exceptions
            //  Use Optionals, Either (Java 9+)
            //
            Optional<Customer> optionalCustomer = Optional.ofNullable(
                    customerService
                            .getCustomerByKey("customer-michele-WRONG-KEY")
                            .thenApply(ApiHttpResponse::getBody)
                            .exceptionally(throwable -> null)
                            .toCompletableFuture().get()
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
                            .toCompletableFuture().get();
                }
                catch (Exception e) { }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
