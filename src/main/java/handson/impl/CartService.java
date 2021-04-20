package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.Cart;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.customer.Customer;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**

 */
public class CartService {

    ApiRoot apiRoot;
    String projectKey;

    public CartService(final ApiRoot client, String projectKey) {
        this.apiRoot = client;
        this.projectKey = projectKey;
    }



    /**
     * Creates a cart for the given customer.
     *
     * @return the customer creation completion stage
     */
    public CompletableFuture<ApiHttpResponse<Cart>> createCart(final ApiHttpResponse<Customer> customerApiHttpResponse) {

        return
                null;
    }


    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart() {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .carts()
                        .post(
                                CartDraftBuilder.of()
                                        .currency("EUR")
                                        .deleteDaysAfterLastModification(90L)
                                        .anonymousId("an" + System.nanoTime())
                                        .country("DE")
                                        .build()
                        )
                        .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> addProductToCartBySkusAndChannel(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final Channel channel,
            final String ... skus) {

        return null;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(
            final ApiHttpResponse<Cart> cartApiHttpResponse, final String code) {

        return null;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> recalculate(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        return null;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShipping(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        return null;
    }
}
