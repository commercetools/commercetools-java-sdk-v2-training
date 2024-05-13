package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.customer.*;
import com.commercetools.api.models.shipping_method.ShippingMethod;
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

        return
            apiRoot
                .inStore(storeKey)
                .carts()
                .withId(cartId)
                .get()
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerSignInResult>> loginCustomer(
            final String customerEmail,
            final String password) {
        CustomerSignin customerSignin = CustomerSigninBuilder.of()
                        .email(customerEmail)
                        .password(password)
                        .build();
        return apiRoot
                .inStore(storeKey)
                .login()
                .post(customerSignin)
                .execute();
    }

    public CompletableFuture<ApiHttpResponse<CustomerSignInResult>> loginCustomer(
            final String customerEmail,
            final String password,
            final String anonymousCartId,
            final AnonymousCartSignInMode anonymousCartSignInMode) {
        CustomerSignin customerSignin = CustomerSigninBuilder.of()
                .email(customerEmail)
                .password(password)
                .anonymousCart(CartResourceIdentifierBuilder.of()
                        .id(anonymousCartId)
                        .build())
                .anonymousCartSignInMode(anonymousCartSignInMode)
                .build();
        return apiRoot
                .inStore(storeKey)
                .login()
                .post(customerSignin)
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
                        .inventoryMode(InventoryMode.NONE)
                )
                .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart() {

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
            final String supplyChannelKey,
            final String distChannelKey,
            final String ... skus) {

        final Cart cart = cartApiHttpResponse.getBody();

        List<CartUpdateAction> cartAddLineItemActions =                         // Cast
            Stream.of(skus)
                .map(s ->
                    CartAddLineItemActionBuilder.of()
                    .sku(s)
                    .quantity(1L)
                    .supplyChannel(
                        channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key(supplyChannelKey)
                    )
                    .distributionChannel(
                        channelResourceIdentifierBuilder -> channelResourceIdentifierBuilder.key(distChannelKey)
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
        final Cart cart,
        final String code) {

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

    public CompletableFuture<ApiHttpResponse<Cart>> recalculate(final Cart cart) {

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

    public CompletableFuture<ApiHttpResponse<Cart>> setShipping(final Cart cart) {

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
