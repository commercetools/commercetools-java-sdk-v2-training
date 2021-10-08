package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.customer.*;
import com.fasterxml.jackson.databind.JsonNode;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpClient;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.commercetools.api.models.customer.AnonymousCartSignInMode.MERGE_WITH_EXISTING_CUSTOMER_CART;
import static com.commercetools.api.models.customer.AnonymousCartSignInMode.USE_AS_NEW_ACTIVE_CUSTOMER_CART;
import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task04c_CART_MERGING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        CustomerService customerService = new CustomerService(client);
        CartService cartService = new CartService(client);
        OrderService orderService = new OrderService(client);
        PaymentService paymentService = new PaymentService(client);
        Logger logger = LoggerFactory.getLogger(Task04c_CART_MERGING.class.getName());

        // TODO:    Inspect cart merging
        //          Complete the checkout by adding products, payment, ... test

        // Get a customer and create a cart for this customer
        //
        final Cart cart = customerService.getCustomerByKey("customer-michael15")
                .thenComposeAsync(cartService::createCart)
                .toCompletableFuture().get()
                .getBody();
        logger.info("cart-id: " + cart.getId());


        // Create an anonymous cart
        //
        Cart anonymousCart = cartService.createAnonymousCart()
                .toCompletableFuture().get()
                .getBody();
        logger.info("cart-id-anonymous: " + anonymousCart.getId());


        // TODO: Decide on a merging strategy
        //
        String cartString = client
                .login()
                .post(
                        CustomerSigninBuilder.of()
                                .anonymousCartSignInMode(MERGE_WITH_EXISTING_CUSTOMER_CART) // Switch to USE_AS_NEW_ACTIVE_CUSTOMER_CART and notice the difference
                                .email("michael15@example.com")
                                .password("password")
                                .anonymousCart(CartResourceIdentifierBuilder.of()
                                        .id(anonymousCart.getId())
                                        .build())
                                .build()
                )
                .execute()
                .toCompletableFuture().get().getBody().getCart().getId();
        logger.info("cart-id-after_merge: " + cartString);

        // TODO: Inspect the customers carts here or via impex
        //

        client.close();
    }
}

