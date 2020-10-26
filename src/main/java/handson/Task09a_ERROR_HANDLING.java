package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.customer.Customer;
import handson.impl.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task09a_ERROR_HANDLING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey("mh-dev-admin.");
        final ApiRoot client = createApiClient("mh-dev-admin.");
        Logger logger = LoggerFactory.getLogger(Task09a_ERROR_HANDLING.class.getName());
        CustomerService customerService = new CustomerService(client, projectKey);


        // Handle 4XX errors


        // TODO: Handle exceptions, CompletionStage
        //
        logger.info("Customer fetch: " +
                customerService
                        .getCustomerByKey("customer-michele-WRONG-KEY")
                        .exceptionally(throwable -> {
                            logger.info("I am not existing");
                            // handle it
                            return  null;
                        })
                        .toCompletableFuture().get()
                        .getBody().getEmail()
        );


        // TODO: Handle exceptions, Optionals, Either (Java 9+)
        //
        Optional<Customer> optionalCustomer = Optional.ofNullable(
                customerService
                        .getCustomerByKey("customer-michele-WRONG-KEY")
                        .toCompletableFuture().get().getBody()
        );

        if (!optionalCustomer.isPresent()) {
            logger.info("I am not existing");
            // handle it
        }

        optionalCustomer.ifPresent(customer -> {
                logger.info("I know that I exist.");
                try {
                        customerService.createEmailVerificationToken(customer, 5)
                        .thenComposeAsync(customerTokenApiHttpResponse -> customerService.verifyEmail(
                                customerTokenApiHttpResponse.getBody()
                        ))
                        .toCompletableFuture().get();
                }
                catch (Exception e) { }
        });

    }
}
