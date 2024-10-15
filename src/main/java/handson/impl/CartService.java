package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressDraft;
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

    public CartService(final ProjectApiRoot client, final String storeKey) {
        this.apiRoot = client;
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
            apiRoot
                .inStore(storeKey)
                .carts()
                .post(
                    cartDraftBuilder -> cartDraftBuilder
                        .currency(currencyCode)
                        .deleteDaysAfterLastModification(90L)
                        .customerEmail(customer.getEmail())
                        .customerId(customer.getId())
                        .country(countryCode)
                        .shippingAddress(customer.getAddresses().stream()
                            .filter(address -> address.getId().equals(customer.getDefaultShippingAddressId()))
                            .findFirst()
                            .orElse(null))
                        .addLineItems(lineItemDraftBuilder -> lineItemDraftBuilder
                                .sku(sku)
                                .supplyChannel(channelResourceIdentifierBuilder ->
                                        channelResourceIdentifierBuilder.key(supplyChannelKey))
                                .distributionChannel(channelResourceIdentifierBuilder ->
                                        channelResourceIdentifierBuilder.key(distChannelKey))
                                .quantity(quantity)
                                .build())
                        .inventoryMode(InventoryMode.NONE)
                )
                .execute();
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


    public CompletableFuture<ApiHttpResponse<Cart>> addProductToCartBySkusAndChannel(
            final ApiHttpResponse<Cart> cartApiHttpResponse,
            final String supplyChannelKey,
            final String distChannelKey,
            final String ... skus) {

        final Cart cart = cartApiHttpResponse.getBody();

        List<CartUpdateAction> cartAddLineItemActions =                         // Cast
            Stream.of(skus)
                .map(sku -> CartAddLineItemActionBuilder.of()
                    .sku(sku)
                    .quantity(1L)
                    .supplyChannel(
                        channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key(supplyChannelKey))
                    .distributionChannel(
                        channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key(distChannelKey))
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
        try {
            return apiRoot
                    .inStore(storeKey)
                    .carts()
                    .withId(cart.getId())
                    .post(
                            cartUpdateBuilder -> cartUpdateBuilder
                                    .version(cart.getVersion())
                                    .plusActions(
                                            cartUpdateActionBuilder -> cartUpdateActionBuilder
                                                    .setShippingAddressBuilder()
                                                    .address(address)
                                    )
                    )
                    .execute();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException();
        }

    }

    public CompletableFuture<ApiHttpResponse<Cart>> freezeCart(final ApiHttpResponse<Cart> cartApiHttpResponse) {

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
                                                CartUpdateActionBuilder::freezeCartBuilder
                                        )
                        )
                        .execute();
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
        System.out.println(shippingMethod.getId());
        return apiRoot
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
