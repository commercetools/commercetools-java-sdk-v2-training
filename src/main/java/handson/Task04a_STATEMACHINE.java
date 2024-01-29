package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.state.State;
import com.commercetools.api.models.state.StateResourceIdentifierBuilder;
import com.commercetools.api.models.state.StateTypeEnum;
import handson.impl.ApiPrefixHelper;
import handson.impl.StateMachineService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static handson.impl.ClientService.createApiClient;


public class Task04a_STATEMACHINE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger(Task04a_STATEMACHINE.class.getName());
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        final StateMachineService stateMachineService = new StateMachineService(client);

        // TODO Use StateMachineService.java to create your designed order state machine
        //

        stateMachineService.createState("mh1OrderPacked", StateTypeEnum.ORDER_STATE, true, "MH1 Order Packed")
                .thenCombineAsync(stateMachineService.createState("mh1OrderShipped", StateTypeEnum.ORDER_STATE, false, "MH1 Order Shipped"),
                        (orderPackedStateApiResponse, orderShippedStateApiResponse)->
                                stateMachineService.setStateTransitions(
                                                orderPackedStateApiResponse.getBody(),
                                                Stream.of(
                                                                StateResourceIdentifierBuilder.of().
                                                                        id(orderShippedStateApiResponse.getBody().getId())
                                                                        .build()
                                                        )
                                                        .collect(Collectors.toList())
                                        )
                                        .thenComposeAsync(apiHttpResponse ->
                                                stateMachineService.setStateTransitions(
                                                        orderShippedStateApiResponse.getBody(),
                                                        new ArrayList<>()
                                                )
                                        )
                )
                .get()
                .thenApply(ApiHttpResponse::getBody)
                .thenAccept(resource -> logger.info("State info {}",resource.getId()))
                .exceptionally(exception -> {
                    logger.info("An error occured " + exception.getMessage());
                    return null;}
                )
                .thenRun(() -> client.close());

        // TODO Create an order in the Merchant Center and verify that custom workflow states are available
        //
    }
}
