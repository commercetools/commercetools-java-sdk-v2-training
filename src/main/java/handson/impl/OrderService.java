package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.order.OrderPagedSearchResponse;
import com.commercetools.api.models.order.OrderState;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Order}s.
 */
public class OrderService {

    final ProjectApiRoot apiRoot;

    public OrderService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<Order>> createOrder(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();

        return
            apiRoot
                .orders()
                .post(
                    orderFromCartDraftBuilder -> orderFromCartDraftBuilder
                        .cart(cartResourceIdentifierBuilder -> cartResourceIdentifierBuilder.id(cart.getId()))
                        .version(cart.getVersion())
                )
                .execute();
    }


    public CompletableFuture<ApiHttpResponse<Order>> changeState(
            final ApiHttpResponse<Order> orderApiHttpResponse,
            final OrderState state) {

        Order order = orderApiHttpResponse.getBody();

        return
            apiRoot
                .orders()
                .withId(order.getId())
                .post(
                    orderUpdateBuilder -> orderUpdateBuilder
                        .version(order.getVersion())
                        .plusActions(
                            orderUpdateActionBuilder -> orderUpdateActionBuilder.changeOrderStateBuilder()
                                .orderState(state)
                        )
                )
                .execute();
    }


    public CompletableFuture<ApiHttpResponse<Order>> changeWorkflowState(
            final ApiHttpResponse<Order> orderApiHttpResponse,
            final String workflowStateKey) {

        Order order = orderApiHttpResponse.getBody();

        return
            apiRoot
                .orders()
                .withId(order.getId())
                .post(
                    orderUpdateBuilder -> orderUpdateBuilder
                        .version(order.getVersion())
                        .plusActions(
                            orderUpdateActionBuilder -> orderUpdateActionBuilder.transitionStateBuilder()
                                .state(stateResourceIdentifierBuilder -> stateResourceIdentifierBuilder.key(workflowStateKey))
                        )
                )
                .execute();
    }

}
