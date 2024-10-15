package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.order.StagedOrderUpdateAction;
import com.commercetools.api.models.order.StagedOrderUpdateActionBuilder;
import handson.impl.ApiPrefixHelper;
import handson.impl.CartService;
import handson.impl.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task05d_ORDER_REPLICATE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final String storeKey = getStoreKey(apiClientPrefix);
            CartService cartService = new CartService(client, storeKey);

            final String orderNumber = "CT253979954003083";

            // TODO: REPLICATE your last order

            cartService.replicateOrderByOrderNumber(orderNumber)
                    .thenAccept(cartApiHttpResponse ->
                            logger.info("cart {} created", cartApiHttpResponse.getBody().getId())
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();
        }
    }
}
