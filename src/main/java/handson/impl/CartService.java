package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.store.Store;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**

 */
public class CartService {

    final ProjectApiRoot apiRoot;
    final String storeKey;

    public CartService(final ProjectApiRoot apiRoot, final String storeKey) {
        this.apiRoot = apiRoot;
        this.storeKey = storeKey;
    }


    public CompletableFuture<ApiHttpResponse<Cart>> getCartById(final String cartId) {

            return apiRoot
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
    public CompletableFuture<ApiHttpResponse<Cart>> createCustomerCart(
            final ApiHttpResponse<Customer> customerApiHttpResponse,
            final ApiHttpResponse<Store> storeApiHttpResponse,
            final String sku,
            final Long quantity,
            final String supplyChannelKey,
            final String distChannelKey) {

        final Customer customer = customerApiHttpResponse.getBody();
        final String countryCode = storeApiHttpResponse.getBody().getCountries().get(0).getCode();
        String currencyCode;
        switch (countryCode) {
            case "US":
                currencyCode = "USD";
                break;
            case "UK":
                currencyCode = "GBP";
                break;
            default:
                currencyCode = "EUR";
                break;
        }
        return
                null;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart(
            final ApiHttpResponse<Store> storeApiHttpResponse,
            final String sku,
            final Long quantity,
            final String supplyChannelKey,
            final String distChannelKey) {

        final String countryCode = storeApiHttpResponse.getBody().getCountries().get(0).getCode();
        String currencyCode;
        switch (countryCode) {
            case "US":
                currencyCode = "USD";
                break;
            case "UK":
                currencyCode = "GBP";
                break;
            default:
                currencyCode = "EUR";
                break;
        }

        return apiRoot
                .inStore(storeKey)
                .carts()
                .post(
                        cartDraftBuilder -> cartDraftBuilder
                                .currency(currencyCode)
                                .deleteDaysAfterLastModification(90L)
                                .anonymousId("an" + System.nanoTime())
                                .country(countryCode)
                                .addLineItems(lineItemDraftBuilder -> lineItemDraftBuilder
                                        .sku(sku)
                                        .supplyChannel(channelResourceIdentifierBuilder ->
                                                channelResourceIdentifierBuilder.key(supplyChannelKey))
                                        .distributionChannel(channelResourceIdentifierBuilder ->
                                                channelResourceIdentifierBuilder.key(distChannelKey))
                                        .quantity(quantity)
                                .build())
                )
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> replicateOrderByOrderNumber(
            final String orderNumber) {

        return
                null;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addProductToCartBySkusAndChannel(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final String supplyChannelKey,
            final String distChannelKey,
            final String ... skus) {

        final Cart cart = cartApiHttpResponse.getBody();

        return
                null;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final String code) {

            final Cart cart = cartApiHttpResponse.getBody();
            return apiRoot
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

    public CompletableFuture<ApiHttpResponse<Cart>> addShippingAddress(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final Address address) {

        final Cart cart = cartApiHttpResponse.getBody();

        return null;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> freezeCart(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();
        return null;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> unfreezeCart(final ApiHttpResponse<Cart> cartApiHttpResponse) {

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
                                                CartUpdateActionBuilder::unfreezeCartBuilder
                                        )
                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> recalculate(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();
        return null;
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShipping(final ApiHttpResponse<Cart> cartApiHttpResponse) {

        final Cart cart = cartApiHttpResponse.getBody();

        return null;
    }

}
