package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartResourceIdentifierBuilder;
import com.commercetools.api.models.customer.AnonymousCartSignInMode;
import com.commercetools.api.models.customer.CustomerSigninBuilder;
import handson.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
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

        CartService cartService = new CartService(client, storeKey);

        final String customerKey = "customer-michael";

        final String supplyChannelKey = "inventory-channel";
        final String distChannelKey = "distribution-channel";

        // TODO:    Inspect cart merging
        //          Complete the checkout by adding products, payment, ... test

        // Get a customer and create a cart for this customer
        //
        final Cart customerCart = customerService.getCustomerByKey(customerKey)
            .thenComposeAsync(cartApiHttpResponse -> cartService.createCart(cartApiHttpResponse))
            .thenComposeAsync(cartApiHttpResponse -> cartService.addProductToCartBySkusAndChannel(
                    cartApiHttpResponse,
                    supplyChannelKey,
                    distChannelKey,
                    "CCM-089", "CCM-089", "CCG-01"))
            .get()
            .getBody();
        logger.info("cart-id: " + customerCart.getId());


        // Create an anonymous cart
        //
        Cart anonymousCart = cartService.createAnonymousCart()
            .thenComposeAsync(cartApiHttpResponse -> cartService.addProductToCartBySkusAndChannel(
                cartApiHttpResponse,
                supplyChannelKey,
                distChannelKey,
                "CCG-01"))
            .get()
            .getBody();
        logger.info("cart-id-anonymous: " + anonymousCart.getId());


        // TODO: Decide on a merging strategy
        //
        final Cart cart = cartService.loginCustomer(
                    "michael@example.com",
                    "password",
                    anonymousCart.getId(),
                    USE_AS_NEW_ACTIVE_CUSTOMER_CART)
                .get().getBody().getCart();

        logger.info("cart ID in use after merge: " + cart.getId());

        cart.getLineItems().forEach(lineItem -> logger.info(lineItem.getVariant().getSku() + " : " + lineItem.getQuantity()));

        client.close();
    }
}

