package handson;

import com.commercetools.api.client.ApiRoot;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import handson.impl.PrefixHelper;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task02b_UPDATE_Group {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = PrefixHelper.getDevApiClientPrefix();

        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());
        final ApiRoot client = createApiClient(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, getProjectKey(apiClientPrefix));

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            // TODO:
            //  GET a customer
            //  GET a customer group
            //  ASSIGN the customer to the customer group
            //
            logger.info("Customer assigned to group: " +
                    customerService
                            .getCustomerByKey("customer-michael15")
                            .thenCombineAsync(
                                    customerService.getCustomerGroupByKey("outdoor-customer-group"),
                                    (customer, customerGroup) ->
                                            customerService.assignCustomerToCustomerGroup(customer.getBody(), customerGroup.getBody())
                                    // .toCompletableFuture().get()             // nicer writing but then unhandled exception in lambda
                            )
                            .thenComposeAsync(CompletableFuture::toCompletableFuture)
                            .exceptionally(throwable -> { logger.info(throwable.getLocalizedMessage()); return null; })
                            .toCompletableFuture().get()
                            .getBody().getEmail()
            );
        }
    }

}

