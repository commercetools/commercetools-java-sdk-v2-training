package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartDraft;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.order.OrderState;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task05a_CHECKOUT {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        final String storeKey = getStoreKey(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, storeKey);

        CartService cartService = new CartService(client, storeKey);
        OrderService orderService = new OrderService(client, storeKey);
        PaymentService paymentService = new PaymentService(client, storeKey);



        // TODO: Fetch a channel if your inventory mode will not be NONE
        //
        final String supplyChannelKey = "inventory-channel";
        final String distChannelKey = "distribution-channel";
        final String initialStateKey = "mhOrderPacked";
        final String customerKey = "customer-michael";

        AtomicReference<Cart> customerCart = new AtomicReference<Cart>();

        // TODO: Perform cart operations:
        //      Get Customer, create cart, add products

        customerService.getCustomerByKey(customerKey)
            .thenComposeAsync(customerApiHttpResponse ->
                cartService.createCart(customerApiHttpResponse))
                    .thenComposeAsync(cartApiHttpResponse ->
                            cartService.addProductToCartBySkusAndChannel(
                                    cartApiHttpResponse,
                                    supplyChannelKey,
                                    distChannelKey,
                                    "CCG-01"))
                    .thenApply(ApiHttpResponse::getBody)
                    .handle((cart, exception) -> {
                        if (exception == null) {
                            logger.info("cart created {}", cart.getId());
                            customerCart.set(cart);
                            return cart;
                        }
                        logger.error("Exception: " + exception.getMessage());
                        return null;
                    }).thenRun(() -> client.close());

//        //  TODO: Get current customer cart in use
//        //  TODO: add discount codes, perform a recalculation
//        //  TODO: add payment
//        //  TODO additionally: add custom line items, add shipping method
//        customerCart.set(cartService.loginCustomer(
//                "michael@example.com",
//                        "password"
//                ).get().getBody().getCart()
//        );
//
//        cartService.addDiscountToCart(customerCart.get(), "BOGO")
//            .thenComposeAsync(cartApiHttpResponse -> cartService.setShipping(cartApiHttpResponse.getBody()))
//            .thenComposeAsync(cartApiHttpResponse -> cartService.recalculate(cartApiHttpResponse.getBody()))
//            .thenComposeAsync(cartApiHttpResponse -> paymentService.createPaymentAndAddToCart(
//                cartApiHttpResponse.getBody(),
//                "We_Do_Payments",
//                "CREDIT_CARD",
//                "we_pay_73636" + Math.random(),    // Must be unique.
//                "pay82626"+ Math.random())                    // Must be unique.
//            )
//            .thenApply(ApiHttpResponse::getBody)
//            .handle((cart, exception) -> {
//                if (exception == null) {
//                    logger.info("cart updated {}", cart.getId());
//                    return cart;
//                }
//                logger.error("Exception: " + exception.getMessage());
//                return null;
//            }).thenRun(() -> client.close());


//        // TODO: Place the order
//        // TODO: Set order status to CONFIRMED, set custom workflow state to intial state
//        customerCart.set(cartService.loginCustomer(
//                "michael@example.com",
//                        "password"
//                ).get().getBody().getCart()
//        );
//
//        orderService.createOrder(customerCart.get())
//            .thenComposeAsync(orderApiHttpResponse -> orderService.changeState(
//                orderApiHttpResponse,
//                OrderState.CONFIRMED
//            ))
//            .thenComposeAsync(orderApiHttpResponse -> orderService.changeWorkflowState(
//                orderApiHttpResponse,
//                initialStateKey
//            ))
//            .thenApply(ApiHttpResponse::getBody)
//            .handle((order, exception) -> {
//                if (exception == null) {
//                    logger.info("Order placed {}", order.getId());
//                    return order;
//                }
//                logger.error("Exception: " + exception.getMessage());
//                return null;
//            }).thenRun(() -> client.close());
    }
}
