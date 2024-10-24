package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
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


public class Task03b_ORDER {

    private static final Logger log = LoggerFactory.getLogger(Task03b_ORDER.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final String storeKey = getStoreKey(apiClientPrefix);

            CartService cartService = new CartService(apiRoot, storeKey);
            CustomerService customerService = new CustomerService(apiRoot, storeKey);
            OrderService orderService = new OrderService(apiRoot, storeKey);
            PaymentService paymentService = new PaymentService(apiRoot, storeKey);

            // TODO: Fetch a channel if your inventory mode will not be NONE
            //
            final String cartId = "";
            final String customerKey = "ct-customer";
            final String customerEmail = "ct@example.com";
            final String orderNumber = "";

            //  TODO: LOGIN customer or signup, if not found
            //


//            // TODO: GET default shipping address from customer profile
//            // TODO Optionally add a new address and save it as default for the customer
//            //
//            customerService
//                    .getCustomerByKey(customerKey)
//                    .thenApply(ApiHttpResponse::getBody)
//                    .thenApply(customer -> customer.getAddresses().stream()
//                            .filter(address -> address.getId().equals(customer.getDefaultShippingAddressId()))
//                            .findFirst()
//                    )
//                    .thenAccept(optionalAddress -> {
//                        Address shippingAddress = optionalAddress.orElseGet(() -> AddressBuilder.of()
//                                .firstName("First")
//                                .lastName("Tester")
//                                .country("DE")
//                                .key(customerKey + "-default")
//                                .build()
//                        );
//                        if(!optionalAddress.isPresent()) {
//                            try {
//                                logger.info("Customer address added and set as default billing and shipping address:"
//                                        + customerService.addAddressToCustomer(customerKey, shippingAddress)
//                                        .get().getBody().getEmail()
//                                );
//                            } catch (Exception e) {throw new RuntimeException(e);}
//                        }
//                        // TODO: UPDATE cart shipping address
//                        // TODO: SET default shipping method
//                        // TODO: RECALCULATE cart
//                    })
//                    .exceptionally(ex -> {
//                        logger.error("Error retrieving customer: {}", ex.getMessage());
//                        return null;
//                    }).join();

//            // TODO ADD Payment to the cart
//            //
//            cartService.getCartById(cartId)
//                    .thenComposeAsync(cartApiHttpResponse ->
//                            paymentService.createPaymentAndAddToCart(
//                                    cartApiHttpResponse.getBody(),
//                                    "We_Do_Payments",
//                                    "CREDIT_CARD",
//                                    "we_pay_73636" + Math.random(),    // Must be unique.
//                                    "pay82626" + Math.random())                  // Must be unique.
//                    )
//                    .thenAccept(cartApiHttpResponse -> logger.info("cart updated with payment {}", cartApiHttpResponse.getBody().getId()))
//                    .exceptionally(throwable -> {
//                        logger.error("Exception: {}", throwable.getMessage());
//                        return null;
//                    }).join();

            // TODO: Place the order
            // TODO: Set order status to CONFIRMED, set custom workflow state to initial state
            // customerService.loginCustomer(customerEmail, "password") // Sign in and find last customer cart
            // or
            // cartService.getCartById(cartId)      // Get cart by Id

        }
    }
}
