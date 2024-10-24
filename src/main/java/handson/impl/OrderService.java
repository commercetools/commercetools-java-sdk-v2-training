package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.order.OrderState;
import com.commercetools.api.models.order.StagedOrderUpdateAction;
import com.commercetools.api.models.order_edit.OrderEdit;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 * This class provides operations to work with {@link Order}s.
 */
public class OrderService {

        final ProjectApiRoot apiRoot;
        final String storeKey;

        public OrderService(final ProjectApiRoot apiRoot, final String storeKey) {
            this.apiRoot = apiRoot;
            this.storeKey = storeKey;
        }

        public CompletableFuture<ApiHttpResponse<Order>> getOrderById(final String orderId) {
            return apiRoot
                    .inStore(storeKey)
                    .orders()
                    .withId(orderId)
                    .get()
                    .execute();
        }

        public CompletableFuture<ApiHttpResponse<Order>> getOrderByOrderNumber(final String orderNumber) {
            return apiRoot
                    .inStore(storeKey)
                    .orders()
                    .withOrderNumber(orderNumber)
                    .get()
                    .execute();
        }

        public CompletableFuture<ApiHttpResponse<Order>> createOrder(final Cart cart) {

            return null;
        }


        public CompletableFuture<ApiHttpResponse<Order>> changeState(
                final ApiHttpResponse<Order> orderApiHttpResponse,
                final OrderState state) {

                Order order = orderApiHttpResponse.getBody();

                return apiRoot
                        .inStore(storeKey)
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
                    null;
        }

        public CompletableFuture<ApiHttpResponse<OrderEdit>> getOrderEditByKey(
                final String orderEditKey) {

            return
                apiRoot
                    .orders()
                    .edits()
                    .withKey(orderEditKey)
                    .get()
                    .withExpand("resource")
                    .execute();
        }

        public CompletableFuture<ApiHttpResponse<OrderEdit>> createOrderEdit(
                final ApiHttpResponse<Order> orderApiHttpResponse,
                final String orderEditKey,
                final StagedOrderUpdateAction stagedOrderUpdateAction) {

            Order order = orderApiHttpResponse.getBody();

            return
                apiRoot
                    .orders()
                    .edits()
                    .post(
                        orderEditDraftBuilder -> orderEditDraftBuilder
                            .stagedActions(stagedOrderUpdateAction)
                            .key(orderEditKey)
                            .resource(orderReferenceBuilder -> orderReferenceBuilder.id(order.getId()))
                    )
                    .execute();
        }

        public CompletableFuture<ApiHttpResponse<OrderEdit>> applyOrderEdit(
                final ApiHttpResponse<OrderEdit> orderEditApiHttpResponse) {

            OrderEdit orderEdit = orderEditApiHttpResponse.getBody();

            return
                    null;
        }

        public CompletableFuture<ApiHttpResponse<Order>> setOrderNumber(
                final ApiHttpResponse<Order> orderApiHttpResponse) {

            Order order = orderApiHttpResponse.getBody();

            return
                    apiRoot
                            .orders()
                            .withId(order.getId())
                            .post(
                                    orderUpdateBuilder -> orderUpdateBuilder
                                            .version(order.getVersion())
                                            .plusActions(orderUpdateActionBuilder -> orderUpdateActionBuilder
                                                    .setOrderNumberBuilder()
                                                    .orderNumber(""))
                            )
                            .execute();
        }

}
