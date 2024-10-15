package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.state.StateResourceIdentifierBuilder;
import com.commercetools.api.models.state.StateTypeEnum;
import handson.impl.ApiPrefixHelper;
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


public class Task04c_STATEMACHINE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");
            final StateMachineService stateMachineService = new StateMachineService(client);

            // TODO Use StateMachineService.java to create your designed order state machine
            //

            stateMachineService.createState(
                            "mhOrderPacked",
                            StateTypeEnum.ORDER_STATE,
                            true,
                            "MH Order Packed"
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: " + throwable.getMessage());
                        try {
                            return stateMachineService.getStateByKey("mhOrderPacked1").get();
                        } catch (Exception e) {
                            client.close();
                            logger.error(e.getMessage());
                            return null;
                        }
                    })
                    .thenCombineAsync(
                            stateMachineService.createState(
                                            "mhOrderShipped",
                                            StateTypeEnum.ORDER_STATE,
                                            false,
                                            "MH Order Shipped"
                                    )
                                    .exceptionally(throwable -> {
                                        logger.error("Exception: " + throwable.getMessage());
                                        try {
                                            return stateMachineService.getStateByKey("mhOrderShipped").get();
                                        } catch (Exception e) {
                                            client.close();
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

            // TODO Create an order in the Merchant Center and verify that custom workflow states are available
            //
        }
    }
}
