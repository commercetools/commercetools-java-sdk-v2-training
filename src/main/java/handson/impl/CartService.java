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


    public CompletableFuture<ApiHttpResponse<Cart>> getCartById(final String cartId, final String storeKey) {

        return
            apiRoot
                .inStore(storeKey)
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
    public CompletableFuture<ApiHttpResponse<Cart>> createCart(final ApiHttpResponse<Customer> customerApiHttpResponse, final String storeKey) {

        final Customer customer = customerApiHttpResponse.getBody();

        return
            apiRoot
                .inStore(storeKey)
                .carts()
                .post(
                    cartDraftBuilder -> cartDraftBuilder
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
                )
                .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart(final String storeKey) {

        return
            apiRoot
                .inStore(storeKey)
                .carts()
                .post(
                    cartDraftBuilder -> cartDraftBuilder
                        .currency("EUR")
                        .deleteDaysAfterLastModification(90L)
                        .anonymousId("an" + System.nanoTime())
                        .country("DE")
                )
                .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> addProductToCartBySkusAndChannel(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final String storeKey,
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
                        channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key(channelKey)
                    )
                    .distributionChannel(
                        channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key(channelKey)
                    )
                    .build()
                )
                .collect(Collectors.toList());

        return
            apiRoot
                .inStore(storeKey)
                .carts()
                .withId(cart.getId())
                .post(
                    cartUpdateBuilder -> cartUpdateBuilder
                        .version(cart.getVersion())
                        .actions(cartAddLineItemActions)
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(
        final ApiHttpResponse<Cart> cartApiHttpResponse,
        final String storeKey,
        final String code) {

        final Cart cart = cartApiHttpResponse.getBody();

        return
            apiRoot
                .inStore(storeKey)
                .carts()
                .withId(cart.getId())
                .post(
                    cartUpdateBuilder -> cartUpdateBuilder
                        .version(cart.getVersion())
                        .plusActions(
                            cartUpdateActionBuilder -> cartUpdateActionBuilder.addDiscountCodeBuilder()
                                .code(code)
                        )
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> recalculate(final ApiHttpResponse<Cart> cartApiHttpResponse, final String storeKey) {

        final Cart cart = cartApiHttpResponse.getBody();

        return
            apiRoot
                .inStore(storeKey)
                .carts()
                .withId(cart.getId())
                .post(
                    cartUpdateBuilder -> cartUpdateBuilder
                        .version(cart.getVersion())
                        .plusActions(
                            cartUpdateActionBuilder -> cartUpdateActionBuilder
                                .recalculateBuilder()
                                .updateProductData(true)
                        )
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShipping(final ApiHttpResponse<Cart> cartApiHttpResponse, final String storeKey) {

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
                .inStore(storeKey)
                .carts()
                .withId(cart.getId())
                .post(
                    cartUpdateBuilder -> cartUpdateBuilder
                        .version(cart.getVersion())
                        .plusActions(
                            cartUpdateActionBuilder -> cartUpdateActionBuilder
                                .setShippingMethodBuilder()
                                .shippingMethod(
                                    shippingMethodResourceIdentifierBuilder -> shippingMethodResourceIdentifierBuilder
                                        .id(shippingMethod.getId())
                                )
                        )
                )
                .execute();
    }

}
