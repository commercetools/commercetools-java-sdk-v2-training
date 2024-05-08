package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.order.OrderState;
import handson.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task05a_CHECKOUT {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        final String storeKey = getStoreKey(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, storeKey);

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
            customerService.getCustomerByKey("customer-michael")
                .thenComposeAsync(customerApiHttpResponse -> cartService.createCart(customerApiHttpResponse, storeKey))
                .thenComposeAsync(cartApiHttpResponse ->
                    cartService.addProductToCartBySkusAndChannel(
                        cartApiHttpResponse,
                        storeKey,
                        channelKey,
                        "TULIPSEED01", "TULIPSEED01")
                )
                .thenComposeAsync(cartApiHttpResponse -> cartService.addDiscountToCart(cartApiHttpResponse, storeKey,"MIXED"))
                .thenComposeAsync(cartApiHttpResponse -> cartService.setShipping(cartApiHttpResponse, storeKey))
                .thenComposeAsync(cartApiHttpResponse -> cartService.recalculate(cartApiHttpResponse, storeKey))
                .thenComposeAsync(cartApiHttpResponse -> paymentService.createPaymentAndAddToCart(
                    cartApiHttpResponse,
                    storeKey,
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
