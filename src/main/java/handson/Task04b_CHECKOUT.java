package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.order.OrderState;
import com.commercetools.api.models.state.State;
import handson.impl.CartService;
import handson.impl.CustomerService;
import handson.impl.OrderService;
import handson.impl.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


/**
 * Create a cart for a customer, add a product to it, create an order from the cart and change the order state.
 *
 * See:
 */
public class Task04b_CHECKOUT {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey("mh-dev-admin.");
        final ApiRoot client = createApiClient("mh-dev-admin.");

        CustomerService customerService = new CustomerService(client, projectKey);
        CartService cartService = new CartService(client, projectKey);
        OrderService orderService = new OrderService(client, projectKey);
        PaymentService paymentService = new PaymentService(client, projectKey);
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());


            // TODO: Fetch a channel if your inventory mode will not be NONE
            //
            Channel channel = client
                        .withProjectKey(projectKey)
                        .channels()
                        .get()
                        .withWhere("key=" + "\"" + "berlin-warehouse" + "\"")                          // See also: .addWhere
                        .execute()
                        .toCompletableFuture().get()
                        .getBody().getResults().get(0);

        final State state = client
                .withProjectKey(projectKey)
                .states()
                .get()
                .withWhere("key=" + "\"" + "OrderPackedNewThird" + "\"")
                .execute()
                .toCompletableFuture().get()
                .getBody().getResults().get(0);


            // TODO: Perform cart operations:
            //      Get Customer, create cart, add products, add inventory mode
            //      add discount codes, perform a recalculation
            // TODO: Convert cart into an order, set order status, set state in custom work
            //
            // TODO: add payment
            // TAKE CARE: Take off payment for second or third try OR change the interfaceID with a timestamp
            //
            // TODO additionally: add custom line items, add shipping method
            //
            logger.info("Created cart/order ID: " +
                    customerService.getCustomerByKey("customer-michele")
                            .thenComposeAsync(customerApiHttpResponse -> cartService.createCart(customerApiHttpResponse.getBody()))

                            .thenComposeAsync(cartApiHttpResponse -> cartService.addProductToCartBySkusAndChannel(
                                    cartApiHttpResponse.getBody(),
                                    channel,
                                    "uzt1286", "uzt1286", "uzt1286")
                            )

                            .thenComposeAsync(cartApiHttpResponse -> cartService.addDiscountToCart(cartApiHttpResponse.getBody(),"AVENSIA"))
                            .thenComposeAsync(cartApiHttpResponse -> cartService.recalculate(cartApiHttpResponse.getBody()))
                            .thenComposeAsync(cartApiHttpResponse -> cartService.setShipping(cartApiHttpResponse.getBody()))

                            .thenComposeAsync(cartApiHttpResponse -> paymentService.createPaymentAndAddToCart(
                                    cartApiHttpResponse.getBody(),
                                    "We_Do_Payments",
                                    "CREDIT_CARD",
                                    "we_pay_73636" + Math.random(),                // Must be unique.
                                    "pay82626"+ Math.random())                // Must be unique.
                            )

                            .thenComposeAsync(cartApiHttpResponse -> orderService.createOrder(cartApiHttpResponse.getBody()))
                            .thenComposeAsync(orderApiHttpResponse -> orderService.changeState(orderApiHttpResponse.getBody(), OrderState.COMPLETE))
                            .thenComposeAsync(orderApiHttpResponse -> orderService.changeWorkflowState(orderApiHttpResponse.getBody(), state))

                            .toCompletableFuture().get()
                            .getBody().getId()
            );

    }
}
