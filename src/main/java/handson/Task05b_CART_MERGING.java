package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.customer.CustomerSigninBuilder;
import handson.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.commercetools.api.models.customer.AnonymousCartSignInMode.MERGE_WITH_EXISTING_CUSTOMER_CART;
import static com.commercetools.api.models.customer.AnonymousCartSignInMode.USE_AS_NEW_ACTIVE_CUSTOMER_CART;
import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task05b_CART_MERGING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        final String storeKey = getStoreKey(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, storeKey);

        CartService cartService = new CartService(client);

        final String customerKey = "customer-michael";

        final String channelKey = "berlin-store-channel";

        // TODO:    Inspect cart merging
        //          Complete the checkout by adding products, payment, ... test

        // Get a customer and create a cart for this customer
        //
        final Cart customerCart = customerService.getCustomerByKey(customerKey)
                .thenComposeAsync(cartApiHttpResponse -> cartService.createCart(cartApiHttpResponse, storeKey))
                .thenComposeAsync(cartApiHttpResponse -> cartService.addProductToCartBySkusAndChannel(
                        cartApiHttpResponse,
                        storeKey,
                        channelKey,
                        "M0E20000000DLQC", "M0E20000000DUX9", "M0E20000000DUX9"
                ))
                .get()
                .getBody();
        logger.info("cart-id: " + customerCart.getId());


        // Create an anonymous cart
        //
        Cart anonymousCart = cartService.createAnonymousCart(storeKey)
            .thenComposeAsync(cartApiHttpResponse -> cartService.addProductToCartBySkusAndChannel(
                cartApiHttpResponse,
                storeKey,
                channelKey,
                "TULIPSEED01"
            ))
            .get()
            .getBody();
        logger.info("cart-id-anonymous: " + anonymousCart.getId());


        // TODO: Decide on a merging strategy
        //
        final Cart cart = client
            .inStore(storeKey)
            .login()
            .post(
                CustomerSigninBuilder.of()
                    .anonymousCartSignInMode(USE_AS_NEW_ACTIVE_CUSTOMER_CART) // Switch to USE_AS_NEW_ACTIVE_CUSTOMER_CART and notice the difference
                    .email("michael@example.com")
                    .password("password")
                    .anonymousCart(CartResourceIdentifierBuilder.of()
                        .id(anonymousCart.getId())
                        .build())
                    .build()
            )
            .execute()
            .get().getBody().getCart();

        logger.info("cart ID in use after merge: " + cart.getId());

        cart.getLineItems().forEach(lineItem -> logger.info(lineItem.getVariant().getSku() + " : " + lineItem.getQuantity()));

        client.close();
    }
}

