package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.common.AddressDraft;
import com.commercetools.api.models.common.AddressDraftBuilder;
import com.commercetools.api.models.customer.AnonymousCartSignInMode;
import com.commercetools.api.models.order.OrderState;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task05b_ORDER {

    private static final Logger log = LoggerFactory.getLogger(Task05b_ORDER.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final String storeKey = getStoreKey(apiClientPrefix);

            CartService cartService = new CartService(client, storeKey);
            CustomerService customerService = new CustomerService(client, storeKey);
            OrderService orderService = new OrderService(client, storeKey);
            PaymentService paymentService = new PaymentService(client, storeKey);

            // TODO: Fetch a channel if your inventory mode will not be NONE
            //
            final String cartId = "992ceff9-6994-4e78-aa76-aa6ccaab7636";
            final String initialStateKey = "mhOrderPacked2";
            final String customerKey = "nd-customer";
            final String customerEmail = "nd@example.de";
            final String orderNumber = "CT253979954003083";

            // TODO: ADD shipping address
            // TODO: SAVE in customer profile as default billing and shipping address

            Address address = AddressBuilder.of()
                    .firstName("Jennifer")
                    .lastName("Tester")
                    .country("DE")
                    .key(customerKey + "-default")
                    .build();

            logger.info("Customer address added and set as default billing and shipping address:"
                    + customerService.addAddressToCustomer(customerKey, address)
                    .get().getBody().getEmail()
            );

            cartService.getCartById(cartId)
                    .thenComposeAsync(cartApiHttpResponse -> cartService.addShippingAddress(cartApiHttpResponse, address))
                    .thenComposeAsync(cartService::setShipping)
                    .thenComposeAsync(cartService::recalculate)
                    .thenAccept(cartApiHttpResponse ->
                                logger.info("cart updated {}", cartApiHttpResponse.getBody().getId())
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();

            // TODO: Place the order
            // TODO: Set order status to CONFIRMED, set custom workflow state to initial state

            // customerService.loginCustomer(customerEmail, "password")


            cartService.getCartById(cartId)
                    .thenApply(cartApiHttpResponse -> {
                            logger.info("Cart ID {}", cartApiHttpResponse.getBody().getId());
                            return cartApiHttpResponse.getBody();
                        }
                    )
                    .thenComposeAsync(orderService::createOrder)
                    // orderService.getOrderByOrderNumber(orderNumber)
                    .thenComposeAsync(orderApiHttpResponse -> orderService.changeState(
                            orderApiHttpResponse,
                            OrderState.CONFIRMED
                    ))
                    .thenComposeAsync(orderApiHttpResponse -> orderService.changeWorkflowState(
                            orderApiHttpResponse,
                            initialStateKey
                    ))
                    .thenAccept(orderApiHttpResponse ->
                            logger.info("Order placed {}", orderApiHttpResponse.getBody().getOrderNumber())
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();
        }
    }
}
