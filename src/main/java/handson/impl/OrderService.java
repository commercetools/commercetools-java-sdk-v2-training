package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.order.*;
import com.commercetools.api.models.state.State;
import com.commercetools.api.models.state.StateResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Order}s.
 */
public class OrderService {

    ApiRoot apiRoot;
    String projectKey;

    public OrderService(final ApiRoot client, String projectKey) {
        this.apiRoot = client;
        this.projectKey = projectKey;
    }

    public CompletableFuture<ApiHttpResponse<Order>> createOrder(final Cart cart) {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .orders()
                        .post(
                                OrderFromCartDraftBuilder.of()
                                    .id(cart.getId())
                                    .version(cart.getVersion())
                                    .build()

                        )
                        .execute();
    }


    public CompletableFuture<ApiHttpResponse<Order>> changeState(final Order order, final OrderState state) {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .orders()
                        .withId(order.getId())
                        .post(
                                OrderUpdateBuilder.of()
                                    .version(order.getVersion())
                                    .actions(
                                            Arrays.asList(
                                                OrderChangeOrderStateActionBuilder.of()
                                                    .orderState(state)
                                                    .build()
                                            )
                                    )
                                    .build()
                        )
                        .execute();
    }


    public CompletableFuture<ApiHttpResponse<Order>> changeWorkflowState(final Order order, final State workflowState) {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .orders()
                        .withId(order.getId())
                        .post(
                                OrderUpdateBuilder.of()
                                        .version(order.getVersion())
                                        .actions(
                                                Arrays.asList(
                                                        OrderTransitionStateActionBuilder.of()
                                                            .state(
                                                                    StateResourceIdentifierBuilder.of()
                                                                        .id(workflowState.getId())
                                                                    .build()
                                                            )
                                                            .build()
                                                )
                                        )
                                        .build()
                        )
                        .execute();
    }

}
