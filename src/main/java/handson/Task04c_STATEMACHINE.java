package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.state.StateResourceIdentifierBuilder;
import com.commercetools.api.models.state.StateTypeEnum;
import handson.impl.ApiPrefixHelper;
import handson.impl.OrderService;
import handson.impl.StateMachineService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task04c_STATEMACHINE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");
            final String storeKey = getStoreKey(apiClientPrefix);

            final StateMachineService stateMachineService = new StateMachineService(apiRoot);
            OrderService orderService = new OrderService(apiRoot, storeKey);

            // TODO Use StateMachineService.java to create your designed order state machine
            //

            stateMachineService.createState(
                            "OrderPacked",
                            StateTypeEnum.ORDER_STATE,
                            true,
                            "Order Packed"
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: " + throwable.getMessage());
                        try {
                            return stateMachineService.getStateByKey("OrderPacked").get();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                            return null;
                        }
                    })
                    .thenCombineAsync(
                            stateMachineService.createState(
                                            "OrderShipped",
                                            StateTypeEnum.ORDER_STATE,
                                            false,
                                            "Order Shipped"
                                    )
                                    .exceptionally(throwable -> {
                                        logger.error("Exception: " + throwable.getMessage());
                                        try {
                                            return stateMachineService.getStateByKey("OrderShipped").get();
                                        } catch (Exception e) {
                                            logger.error(e.getMessage());
                                            return null;
                                        }
                                    }),
                            (orderPackedStateApiResponse, orderShippedStateApiResponse) ->
                                    stateMachineService.setStateTransitions(
                                                    orderShippedStateApiResponse.getBody(),
                                                    new ArrayList<>()
                                            )
                                            .thenComposeAsync(apiHttpResponse ->
                                                    stateMachineService.setStateTransitions(
                                                            orderPackedStateApiResponse.getBody(),
                                                            Stream.of(
                                                                            StateResourceIdentifierBuilder.of().
                                                                                    id(orderShippedStateApiResponse.getBody().getId())
                                                                                    .build()
                                                                    )
                                                                    .collect(Collectors.toList())
                                                    )
                                            )
                    )
                    .get()
                    .thenApply(ApiHttpResponse::getBody)
                    .thenAccept(state -> {
                                logger.info("Initial state key {}", state.getKey());
                            }
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();

//            // TODO Check your last order in the Merchant Center and verify that custom workflow states are available
//            //
//             orderService.getOrderByOrderNumber("")
//                    .thenComposeAsync(orderApiHttpResponse -> orderService.changeWorkflowState(
//                            orderApiHttpResponse,
//                            ""
//                    ))
//                    .thenAccept(orderApiHttpResponse ->
//                            logger.info("Order state updated {}", orderApiHttpResponse.getBody().getOrderNumber())
//                    )
//                    .exceptionally(throwable -> {
//                        logger.error("Exception: {}", throwable.getMessage());
//                        return null;
//                    }).join();
        }
    }
}
