package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;

public class Task02b_UPDATE_Group {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        final String storeKey = getStoreKey(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, storeKey);

        // TODO:
        //  ASSIGN the customer to the customer group
        //

        customerService.assignCustomerToCustomerGroup(
                "customer-michael",
                "vip-customers"
        )
            .thenApply(ApiHttpResponse::getBody)
            .handle((customer, exception) -> {
                if (exception == null) {
                    logger.info("Resource ID: " + customer.getId());
                    return customer;
                };
                logger.error("Exception: " + exception.getMessage());
                return null;
            }).thenRun(() -> client.close());
    }

}

