package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.order.OrderState;
import com.commercetools.api.models.order.StagedOrderUpdateAction;
import com.commercetools.api.models.order.StagedOrderUpdateActionBuilder;
import com.commercetools.api.models.order_edit.StagedOrderAddLineItemActionBuilder;
import handson.impl.*;
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
        OrderService orderService = new OrderService(client);
        final String orderId = "6218d502-6fd1-4bef-a9b7-d4ae7988626b";

        // TODO: Create and Apply an Order Edit

        final StagedOrderUpdateAction stagedOrderUpdateAction = StagedOrderUpdateActionBuilder.of()
                    .addLineItemBuilder()
                    .sku("M0E20000000E93Z")
                    .supplyChannel(channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key("sunrise-store-newyork"))
                    .distributionChannel(channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key("sunrise-store-newyork"))
                .build();

        logger.info("Created OrderEdit: " +
                orderService.getOrderById(orderId)
                    .thenComposeAsync(orderApiHttpResponse -> orderService.createOrderEdit(orderApiHttpResponse, stagedOrderUpdateAction))
                    .get()
                    .getBody()
                    .getResult()
        );

//        final String orderEditId = "";
//        logger.info("Applied OrderEdit: " +
//                orderService.getOrderEditById(orderEditId)
//                    .thenComposeAsync(orderEditApiHttpResponse -> orderService.applyOrderEdit(orderEditApiHttpResponse))
//                    .get()
//                    .getBody()
//                    .getResult().getType()
//
//        );

        client.close();
    }
}
