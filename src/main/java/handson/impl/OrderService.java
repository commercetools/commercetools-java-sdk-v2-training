package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.order.*;
import com.commercetools.api.models.state.State;
import com.commercetools.api.models.state.StateResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public CompletableFuture<ApiHttpResponse<Order>> createOrder(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();

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


    public CompletableFuture<ApiHttpResponse<Order>> changeState(
            final ApiHttpResponse<Order> orderApiHttpResponse,
            final OrderState state) {

        Order order = orderApiHttpResponse.getBody();

        List<OrderUpdateAction> orderUpdateActions = new ArrayList<>();

        orderUpdateActions.add(
                OrderChangeOrderStateActionBuilder.of()
                        .orderState(state)
                        .build()
        );

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .orders()
                        .withId(order.getId())
                        .post(
                                OrderUpdateBuilder.of()
                                    .version(order.getVersion())
                                    .actions(orderUpdateActions)
                                    .build()
                        )
                        .execute();
    }


    public CompletableFuture<ApiHttpResponse<Order>> changeWorkflowState(
            final ApiHttpResponse<Order> orderApiHttpResponse,
            final State workflowState) {

        Order order = orderApiHttpResponse.getBody();

        List<OrderUpdateAction> updateActions = new ArrayList<>();
        updateActions.add(
                OrderTransitionStateActionBuilder.of()
                        .state(
                                StateResourceIdentifierBuilder.of()
                                        .id(workflowState.getId())
                                        .build()
                        )
                        .build()
        );

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .orders()
                        .withId(order.getId())
                        .post(
                                OrderUpdateBuilder.of()
                                        .version(order.getVersion())
                                        .actions(updateActions)
                                        .build()
                        )
                        .execute();
    }

}
