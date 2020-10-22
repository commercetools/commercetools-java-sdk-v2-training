package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.customer.*;
import com.fasterxml.jackson.databind.JsonNode;
import handson.impl.CartService;
import handson.impl.CustomerService;
import handson.impl.OrderService;
import handson.impl.PaymentService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.commercetools.api.models.customer.AnonymousCartSignInMode.MERGE_WITH_EXISTING_CUSTOMER_CART;
import static com.commercetools.api.models.customer.AnonymousCartSignInMode.USE_AS_NEW_ACTIVE_CUSTOMER_CART;
import static handson.impl.ClientService.createApiClient;


public class Task04c_CART_MERGING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = "training-011-avensia-test";
        final ApiRoot client = createApiClient("mh-dev-admin.");

        CustomerService customerService = new CustomerService(client, projectKey);
        CartService cartService = new CartService(client, projectKey);
        OrderService orderService = new OrderService(client, projectKey);
        PaymentService paymentService = new PaymentService(client, projectKey);
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

            // TODO: cart merging
            // complete, add products, payment, ... test

            // TODO Create a cart for this customer
            //
            final Customer customer =
                    customerService.createCustomer(
                            "michael12@example.com",
                            "password",
                            "customer-michael12",
                            "michael",
                            "hartwig",
                            "DE")
                        .toCompletableFuture().get()
                        .getBody().getCustomer();

            // TODO: Create anonymous cart
            //
            Cart anonymousCart = cartService.createAnonymousCart()
                .toCompletableFuture().get()
                .getBody();



            // TODO: Decide on a merging strategy
            //
            client
                .withProjectKey(projectKey)
                .login()
                .post(
                        CustomerSigninBuilder.of()
                            .anonymousCartSignInMode(MERGE_WITH_EXISTING_CUSTOMER_CART)
                            .build()
                    )
                .execute();
            client
                .withProjectKey(projectKey)
                .login()
                .post(
                        CustomerSigninBuilder.of()
                                .anonymousCartSignInMode(USE_AS_NEW_ACTIVE_CUSTOMER_CART)
                                .build()
                )
                .execute();

    }
}

