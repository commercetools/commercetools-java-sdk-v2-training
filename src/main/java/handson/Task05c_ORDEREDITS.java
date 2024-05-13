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

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        final String storeKey = getStoreKey(apiClientPrefix);
        OrderService orderService = new OrderService(client, storeKey);
        final String supplyChannelKey = "inventory-channel";
        final String distChannelKey = "distribution-channel";

        final String orderId = "e85cd198-5254-4679-abaa-b6fc7b59abd1";

        // TODO: Create and Apply an Order Edit

        final StagedOrderUpdateAction stagedOrderUpdateAction = StagedOrderUpdateActionBuilder.of()
                    .addLineItemBuilder()
                    .sku("CCM-089")
                    .supplyChannel(channelResourceIdentifierBuilder ->
                            channelResourceIdentifierBuilder.key(supplyChannelKey))
                    .distributionChannel(channelResourceIdentifierBuilder ->
                            channelResourceIdentifierBuilder.key(distChannelKey))
                .build();

        orderService.getOrderById(orderId)
            .thenComposeAsync(orderApiHttpResponse -> orderService.createOrderEdit(
                    orderApiHttpResponse,
                    "mh-orderedit",
                    stagedOrderUpdateAction))
                .thenApply(ApiHttpResponse::getBody)
                .handle((orderEdit, exception) -> {
                    if (exception == null) {
                        logger.info("orderEdit created {}", orderEdit.getResult().getType());
                        return orderEdit;
                    }
                    logger.error("Exception: " + exception.getMessage());
                    return null;
                }).thenRun(() -> client.close());

//           //  TODO: Apply OrderEdit
//            orderService.getOrderEditByKey("mh-orderedit")
//                .thenComposeAsync(orderEditApiHttpResponse -> orderService.applyOrderEdit(orderEditApiHttpResponse))
//                    .thenApply(ApiHttpResponse::getBody)
//                    .handle((orderEdit, exception) -> {
//                        if (exception == null) {
//                            logger.info("orderEdit created {}", orderEdit.getResult().getType());
//                            return orderEdit;
//                        }
//                        logger.error("Exception: " + exception.getMessage());
//                        return null;
//                    }).thenRun(() -> client.close());
    }
}
