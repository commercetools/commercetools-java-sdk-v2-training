package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.cart.*;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.channel.ChannelResourceIdentifierBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.shipping_method.ShippingMethod;
import com.commercetools.api.models.shipping_method.ShippingMethodResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * @param customer the customer
     * @return the customer creation completion stage
     */
    public CompletableFuture<ApiHttpResponse<Cart>> createCart(final Customer customer) {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .carts()
                        .post(
                                CartDraftBuilder.of()
                                        .currency("EUR")
                                        .deleteDaysAfterLastModification(90l)
                                        .customerEmail(customer.getEmail())
                                        .customerId(customer.getId())
                                        .country(
                                                customer.getAddresses().stream()
                                                    .filter(a -> a.getId().equals(customer.getDefaultShippingAddressId()))
                                                    .findFirst()
                                                    .get()
                                                    .getCountry()
                                        )
                                        .shippingAddress(customer.getAddresses().stream()
                                                .filter(a -> a.getId().equals(customer.getDefaultShippingAddressId()))
                                                .findFirst()
                                                .get()
                                        )
                                        .inventoryMode(InventoryMode.RESERVE_ON_ORDER)
                                        .build()
                        )
                        .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> createAnonymousCart() {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .carts()
                        .post(
                                CartDraftBuilder.of()
                                        .currency("EUR")
                                        .deleteDaysAfterLastModification(90l)
                                        .anonymousId("123456789")
                                        .country("DE")
                                        .build()
                        )
                        .execute();
    }


    public CompletableFuture<ApiHttpResponse<Cart>> addProductToCartBySkusAndChannel(final Cart cart, final Channel channel, final String ... skus) {

        List<CartUpdateAction> cartAddLineItemActions =                         // Cast
                Stream.of(skus)
                    .map(s ->
                            CartAddLineItemActionBuilder.of()
                            .sku(s)
                            // .quantity(Double.valueOf(1L))                    // Setting the quantity requires double but json expects long
                            .supplyChannel(
                                    ChannelResourceIdentifierBuilder.of()
                                            .id(channel.getId())
                                            .build()
                            )
                            .build()
                    )
                    .collect(Collectors.toList());

        return
                apiRoot
                        .withProjectKey(projectKey)
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

    public CompletableFuture<ApiHttpResponse<Cart>> addDiscountToCart(final Cart cart, final String code) {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .carts()
                        .withId(cart.getId())
                        .post(
                                CartUpdateBuilder.of()
                                        .version(cart.getVersion())
                                        .actions(
                                                Arrays.asList(
                                                        CartAddDiscountCodeActionBuilder.of()
                                                            .code(code)
                                                            .build()
                                                )
                                        )
                                        .build()

                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> recalculate(final Cart cart) {

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .carts()
                        .withId(cart.getId())
                        .post(
                                CartUpdateBuilder.of()
                                        .version(cart.getVersion())
                                        .actions(
                                                Arrays.asList(
                                                        CartRecalculateActionBuilder.of()
                                                                .build()
                                                )
                                        )
                                        .build()

                        )
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<Cart>> setShipping(final Cart cart) {


        final ShippingMethod shippingMethod =
                apiRoot
                    .withProjectKey(projectKey)
                    .shippingMethods()
                    .matchingCart()
                    .get()
                    .withCartId(cart.getId())
                    .executeBlocking()
                    .getBody().getResults().get(0);

        return
                apiRoot
                        .withProjectKey(projectKey)
                        .carts()
                        .withId(cart.getId())
                        .post(
                                CartUpdateBuilder.of()
                                        .version(cart.getVersion())
                                        .actions(
                                                Arrays.asList(
                                                        CartSetShippingMethodActionBuilder.of()
                                                                .shippingMethod(
                                                                        ShippingMethodResourceIdentifierBuilder.of()
                                                                            .id(shippingMethod.getId())
                                                                            .build()
                                                                )
                                                                .build()
                                                )
                                        )
                                        .build()
                        )
                        .execute();
    }



}
