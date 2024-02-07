package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.order.OrderState;
import com.commercetools.api.models.state.State;
import handson.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Task04b_CHECKOUT {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        CustomerService customerService = new CustomerService(client);
        CartService cartService = new CartService(client);
        OrderService orderService = new OrderService(client);
        PaymentService paymentService = new PaymentService(client);



        // TODO: Fetch a channel if your inventory mode will not be NONE
        //
        final String channelKey = "berlin-store-channel";
        final String initialStateKey = "mhOrderPacked";

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
                customerService.getCustomerByKey("customer-michael15")
                        .thenComposeAsync(cartService::createCart)
                        .thenComposeAsync(cartApiHttpResponse ->
                                cartService.addProductToCartBySkusAndChannel(
                                    cartApiHttpResponse,
                                    channelKey,
                                    "TULIPSEED01", "TULIPSEED01", "TULIPSEED02")
                        )
                        .thenComposeAsync(cartApiHttpResponse -> cartService.addDiscountToCart(cartApiHttpResponse,"MIXED"))
                        .thenComposeAsync(cartService::setShipping)
                        .thenComposeAsync(cartService::recalculate)
                        .thenComposeAsync(cartApiHttpResponse -> paymentService.createPaymentAndAddToCart(
                                cartApiHttpResponse,
                                "We_Do_Payments",
                                "CREDIT_CARD",
                                "we_pay_73636" + Math.random(),                // Must be unique.
                                "pay82626"+ Math.random())                    // Must be unique.
                        )

                        .thenComposeAsync(orderService::createOrder)
                        .thenComposeAsync(orderApiHttpResponse -> orderService.changeState(orderApiHttpResponse, OrderState.CONFIRMED))
                        .thenComposeAsync(orderApiHttpResponse -> orderService.changeWorkflowState(orderApiHttpResponse, initialStateKey))

                        .get()
                        .getBody().getId()
        );

        client.close();
    }
}
