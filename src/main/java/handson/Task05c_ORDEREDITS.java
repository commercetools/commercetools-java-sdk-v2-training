package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.order.OrderState;
import com.commercetools.api.models.order.StagedOrderUpdateAction;
import com.commercetools.api.models.order.StagedOrderUpdateActionBuilder;
import com.commercetools.api.models.order_edit.StagedOrderAddLineItemActionBuilder;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task05c_ORDEREDITS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();

        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final String storeKey = getStoreKey(apiClientPrefix);
            OrderService orderService = new OrderService(client, storeKey);
            final String supplyChannelKey = "boston-store-channel";
            final String distChannelKey = "boston-store-channel";

            final String orderNumber = "CT463742039052500";
            final String orderEditKey = "CTOE-464185882118958";

            // TODO: Create and Apply an Order Edit
            //
            final StagedOrderUpdateAction stagedOrderUpdateAction = StagedOrderUpdateActionBuilder.of()
                    .addLineItemBuilder()
                    .sku("RCC-09")
                    .supplyChannel(channelResourceIdentifierBuilder ->
                            channelResourceIdentifierBuilder.key(supplyChannelKey))
                    .distributionChannel(channelResourceIdentifierBuilder ->
                            channelResourceIdentifierBuilder.key(distChannelKey))
                    .build();

            orderService.getOrderByOrderNumber(orderNumber)
                    .thenComposeAsync(orderApiHttpResponse ->
                            orderService.createOrderEdit(
                                    orderApiHttpResponse,
                                    "CTOE-" + System.nanoTime(),
                                    stagedOrderUpdateAction))
                    .thenAccept(orderEditApiHttpResponse ->
                            logger.info("orderEdit {} created with {} type", orderEditApiHttpResponse.getBody().getKey(), orderEditApiHttpResponse.getBody().getResult().getType())
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();
            // TODO update orderEditKey above


//            //  TODO: Apply OrderEdit
//            //
//            orderService.getOrderEditByKey(orderEditKey)
//                    .thenComposeAsync(orderService::applyOrderEdit)
//                    .thenAccept(orderEditApiHttpResponse ->
//                            logger.info("orderEdit {} ", orderEditApiHttpResponse.getBody().getResult().getType())
//                    )
//                    .exceptionally(throwable -> {
//                        logger.error("Exception: {}", throwable.getMessage());
//                        return null;
//                    }).join();
        }
    }
}
