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
            final String initialStateKey = "OrderPacked";
            final String customerKey = "ct-customer";
            final String customerEmail = "ct@example.de";
            final String orderNumber = "CT253979954003083";

            //  TODO: LOGIN customer or signup, if not found
            //  TODO: add discount codes, perform a recalculation
            //  TODO: add payment
            //  TODO additionally: add custom line items, add shipping method
            customerService.loginCustomer(
                            customerEmail,
                            "password",
                            cartId,
                            AnonymousCartSignInMode.USE_AS_NEW_ACTIVE_CUSTOMER_CART
                    )
                    .exceptionally(ex -> {
                        logger.info("exception: {}", ex.getMessage());
                        try {
                            return customerService.createCustomer(
                                    customerEmail,
                                    "password",
                                    cartId
                            ).get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .thenAccept(customerSignInResult -> {
                        logger.info("cart updated {}", customerSignInResult.getBody().getCart().getId());
                    }).join();

            // TODO: ADD shipping address
            //
            customerService
                    .getCustomerByKey(customerKey)
                    .thenApply(ApiHttpResponse::getBody)
                    .thenApply(customer -> customer.getAddresses().stream()
                            .filter(address -> address.getId().equals(customer.getDefaultShippingAddressId()))
                            .findFirst()
                    )
                    .thenAccept(optionalAddress -> {
                        Address shippingAddress = optionalAddress.orElseGet(() -> AddressBuilder.of()
                                .firstName("First")
                                .lastName("Tester")
                                .country("DE")
                                .key(customerKey + "-default")
                                .build()
                        );
                        try {
                            logger.info("Customer address added and set as default billing and shipping address:"
                                    + customerService.addAddressToCustomer(customerKey, shippingAddress)
                                    .get().getBody().getEmail()
                            );
                        } catch (Exception e) {throw new RuntimeException(e);}

                        cartService.getCartById(cartId)
                                .thenComposeAsync(cartApiHttpResponse -> cartService.addShippingAddress(cartApiHttpResponse, shippingAddress))
                                .thenComposeAsync(cartService::setShipping)
                                .thenComposeAsync(cartService::recalculate)
                                .thenAccept(cartApiHttpResponse -> {
                                    logger.info("cart updated with shipping info {}", cartApiHttpResponse.getBody().getId());
                                })
                                .exceptionally(throwable -> {
                                    logger.error("Exception: {}", throwable.getMessage());
                                    return null;
                                }).join();
                    })
                    .exceptionally(ex -> {
                        logger.error("Error retrieving customer: {}", ex.getMessage());
                        return null;
                    });

            // TODO ADD Payment to the cart
            cartService.getCartById(cartId)
                    .thenComposeAsync(cartApiHttpResponse ->
                            paymentService.createPaymentAndAddToCart(
                                    cartApiHttpResponse.getBody(),
                                    "We_Do_Payments",
                                    "CREDIT_CARD",
                                    "we_pay_73636" + Math.random(),    // Must be unique.
                                    "pay82626" + Math.random())                  // Must be unique.
                    )
                    .thenAccept(cartApiHttpResponse -> logger.info("cart updated with payment {}", cartApiHttpResponse.getBody().getId()))
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
