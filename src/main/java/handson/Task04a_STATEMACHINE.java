package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.state.State;
import com.commercetools.api.models.state.StateReferenceBuilder;
import com.commercetools.api.models.state.StateResourceIdentifierBuilder;
import com.commercetools.api.models.state.StateTypeEnum;
import handson.impl.StateMachineService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static handson.impl.ClientService.createApiClient;


public class Task04a_STATEMACHINE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = Logger.getLogger(Task02a_CREATE.class.getName());
        final ApiRoot client = createApiClient("mh-dev-admin.");
        final StateMachineService stateMachineService = new StateMachineService(client, "training-011-avensia-test");

            // TODO
            // Use StateMachineService.java to create your designed order state machine
            //
            State orderPackedState =
                    stateMachineService.createState("OrderPacked", StateTypeEnum.ORDER_STATE, true, "Order Packed")
                    .toCompletableFuture().get()
                    .getBody();
            State orderShippedState =
                    stateMachineService.createState("OrderShipped", StateTypeEnum.ORDER_STATE, false, "Order Shipped")
                    .toCompletableFuture().get()
                    .getBody();

            logger.info("State info {}" +
                    stateMachineService.setStateTransitions(
                            orderPackedState,
                            Stream.of(
                                    StateResourceIdentifierBuilder.of().
                                            id(orderShippedState.getId())
                                            .build()
                                )
                                .collect(Collectors.toList())
                    )
                    .toCompletableFuture().get()
            );


    }
}
