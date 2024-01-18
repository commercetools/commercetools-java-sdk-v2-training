package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.channel.ChannelResourceIdentifierBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.shipping_method.ShippingMethodResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**

 */
public class CartService {

    final ProjectApiRoot apiRoot;

    public CartService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }


    public CompletableFuture<ApiHttpResponse<Cart>> getCartById(final String cartId) {

        return
                apiRoot
                        .carts()
                        .withId(cartId)
                        .get()
                        .execute();
    }
    /**
     * Creates a cart for the given customer.
     *
     * @return the customer creation completion stage
     */
    public CompletableFuture<ApiHttpResponse<Cart>> createCart(final ApiHttpResponse<Customer> customerApiHttpResponse) {

        final Customer customer = customerApiHttpResponse.getBody();

        return
                apiRoot
                        .carts()
                        .post(
                                CartDraftBuilder.of()
                                        .currency("EUR")
                                        .deleteDaysAfterLastModification(90L)
                                        .customerEmail(customer.getEmail())
                                        .customerId(customer.getId())
                                        .country(
                                                customer.getAddresses().stream()
                                                    .filter(a -> a.getId().equals(customer.getDefaultShippingAddressId()))
                                                    .findFirst()
                                                        .orElse(null)
                                                    .getCountry()
                                        )
                                        .shippingAddress(customer.getAddresses().stream()
                                                .filter(a -> a.getId().equals(customer.getDefaultShippingAddressId()))
                                                .findFirst()
                                                .orElse(null)
                                        )
                                        .inventoryMode(InventoryMode.RESERVE_ON_ORDER)
                                        .build()
                        )
                        .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart() {

        return
                apiRoot
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
            final String channelKey,
            final String ... skus) {

        final Cart cart = cartApiHttpResponse.getBody();

        List<CartUpdateAction> cartAddLineItemActions =                         // Cast
                Stream.of(skus)
                    .map(s ->
                            CartAddLineItemActionBuilder.of()
                            .sku(s)
                            .quantity(1L)
                            .supplyChannel(
                                    ChannelResourceIdentifierBuilder.of()
                                            .key(channelKey)
                                            .build()
                            )
                            .distributionChannel(
                                    ChannelResourceIdentifierBuilder.of()
                                            .key(channelKey)
                                            .build()
                            )
                            .build()
                    )
                    .collect(Collectors.toList());

        return
                apiRoot
                        .carts()
                        .withId(cart.getId())
                        .post(
                                CartUpdateBuilder.of()
                                    .version(cart.getVersion())
                                    .actions(
                                            cartAddLineItemActions
                                    )
                                    .build()

                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(
            final ApiHttpResponse<Cart> cartApiHttpResponse, final String code) {

        final Cart cart = cartApiHttpResponse.getBody();

        return
                apiRoot
                        .carts()
                        .withId(cart.getId())
                        .post(
                                CartUpdateBuilder.of()
                                        .version(cart.getVersion())
                                        .actions(
                                            CartAddDiscountCodeActionBuilder.of()
                                                .code(code)
                                                .build()
                                        )
                                        .build()

                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> recalculate(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();

        return
                apiRoot
                        .carts()
                        .withId(cart.getId())
                        .post(
                                CartUpdateBuilder.of()
                                        .version(cart.getVersion())
                                        .actions(
                                                CartRecalculateActionBuilder.of()
                                                        .build()
                                        )
                                        .build()

                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShipping(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();

        final ShippingMethod shippingMethod =
                apiRoot
                    .shippingMethods()
                    .matchingCart()
                    .get()
                    .withCartId(cart.getId())
                    .executeBlocking()
                    .getBody().getResults().get(0);

        return
                apiRoot
                        .carts()
                        .withId(cart.getId())
                        .post(
                                CartUpdateBuilder.of()
                                        .version(cart.getVersion())
                                        .actions(
                                            CartSetShippingMethodActionBuilder.of()
                                                    .shippingMethod(
                                                            ShippingMethodResourceIdentifierBuilder.of()
                                                                .id(shippingMethod.getId())
                                                                .build()
                                                    )
                                                    .build()
                                        )
                                        .build()
                        )
                        .execute();
    }

}
