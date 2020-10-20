package handson;

import com.commercetools.api.client.ApiRoot;
import handson.impl.ClientService;
import handson.impl.CustomerService;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
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
public class Task02b_UPDATE_Group {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = Logger.getLogger(Task02b_UPDATE_Group.class.getName());
        final ApiRoot client = createApiClient("mh-dev-admin.");
        CustomerService customerService = new CustomerService(client, "barbara-merchant-center");

        // TODO:
        //  GET a customer
        //  GET a customer group
        //  ASSIGN the customer to the customer group
        //
        logger.log(Level.INFO, "Customer assigned to group: " +
                customerService
                    .getCustomerByKey("customer-barbara")
                    .thenCombineAsync(
                            customerService.getCustomerGroupByKey("outdoor"),
                            (customer, customerGroup) ->
                                    customerService.updateCustomerAssigningCustomerGroup(customer.getBody(), customerGroup.getBody())
                                    // .toCompletableFuture().get()             // nicer writing but then unhandled exception in lambda
                    )
                    .thenComposeAsync(CompletableFuture::toCompletableFuture)
                    .exceptionally(throwable -> { logger.info(throwable.getLocalizedMessage()); return null; })
                    .toCompletableFuture().get()
                    .getBody().getCustomerGroup()
        );
    }

}

