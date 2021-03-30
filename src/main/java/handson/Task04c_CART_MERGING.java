package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.customer.CustomerSigninBuilder;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.commercetools.api.models.customer.AnonymousCartSignInMode.MERGE_WITH_EXISTING_CUSTOMER_CART;
import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task04c_CART_MERGING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {

            CustomerService customerService = new CustomerService(client, projectKey);
            CartService cartService = new CartService(client, projectKey);
            OrderService orderService = new OrderService(client, projectKey);
            PaymentService paymentService = new PaymentService(client, projectKey);
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
                    .withProjectKey(projectKey)
                    .login()
                    .post(
                            CustomerSigninBuilder.of()
                                    .anonymousCartSignInMode(MERGE_WITH_EXISTING_CUSTOMER_CART)
                                    .email("michael15@example.com")
                                    .password("password")
                                    .anonymousCartId(anonymousCart.getId())
                                    .build()
                    )
                    .execute()
                    .toCompletableFuture().get().getBody().getCart().getId();
            logger.info("cart-id-after_merge: " + cartString);


            /*
            client
                .withProjectKey(projectKey)
                .login()
                .post(
                        CustomerSigninBuilder.of()
                                .anonymousCartSignInMode(USE_AS_NEW_ACTIVE_CUSTOMER_CART)
                                .build()
                )
                .execute();
            */

            // TODO: Inspect the customers carts here or via impex
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

