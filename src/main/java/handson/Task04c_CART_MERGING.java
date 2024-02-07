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
import java.util.stream.Collectors;

import static com.commercetools.api.models.customer.AnonymousCartSignInMode.MERGE_WITH_EXISTING_CUSTOMER_CART;
import static handson.impl.ClientService.createApiClient;


public class Task04c_CART_MERGING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        CustomerService customerService = new CustomerService(client);
        CartService cartService = new CartService(client);

        final String customerKey = "customer-michael15";

        final String channelKey = "berlin-store-channel";

        // TODO:    Inspect cart merging
        //          Complete the checkout by adding products, payment, ... test

        // Get a customer and create a cart for this customer
        //
        final Cart customerCart = customerService.getCustomerByKey(customerKey)
                .thenComposeAsync(cartService::createCart)
                .thenComposeAsync(cartApiHttpResponse -> cartService.addProductToCartBySkusAndChannel(
                        cartApiHttpResponse,
                        channelKey,
                        "TULIPSEED01", "TULIPSEED01", "TULIPSEED02"
                ))
                .get()
                .getBody();
        logger.info("cart-id: " + customerCart.getId());


        // Create an anonymous cart
        //
        Cart anonymousCart = cartService.createAnonymousCart()
                .thenComposeAsync(cartApiHttpResponse -> cartService.addProductToCartBySkusAndChannel(
                        cartApiHttpResponse,
                        channelKey,
                        "TULIPSEED01"
                ))
                .get()
                .getBody();
        logger.info("cart-id-anonymous: " + anonymousCart.getId());


        // TODO: Decide on a merging strategy
        //
        final Cart cart = client
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
                .get().getBody().getCart();

        logger.info("cart ID in use after merge: " + cart.getId());

        cart.getLineItems().forEach(lineItem -> logger.info(lineItem.getVariant().getSku()));

        client.close();
    }
}

