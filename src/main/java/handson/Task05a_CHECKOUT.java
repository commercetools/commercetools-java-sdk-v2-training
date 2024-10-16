package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.common.AddressDraft;
import com.commercetools.api.models.common.AddressDraftBuilder;
import com.commercetools.api.models.customer.AnonymousCartSignInMode;
import com.commercetools.api.models.customer.Customer;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task05a_CHECKOUT {

    private static final Logger log = LoggerFactory.getLogger(Task05a_CHECKOUT.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String supplyChannelKey = "";
        final String distChannelKey = "";
        final String customerKey = "";
        final String customerEmail = "";
        final String anonymousCartId = "";

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {

            Logger logger = LoggerFactory.getLogger("commercetools");
            final String storeKey = getStoreKey(apiClientPrefix);

            CustomerService customerService = new CustomerService(client, storeKey);
            CartService cartService = new CartService(client, storeKey);
            OrderService orderService = new OrderService(client, storeKey);
            PaymentService paymentService = new PaymentService(client, storeKey);
            StoreService storeService = new StoreService(client, storeKey);

            // TODO: GET the products in the store
            storeService.getProductsInCurrentStore()
                    .thenApply(ApiHttpResponse::getBody)
                    .thenAccept(productsInStorePagedQueryResponse -> {
                            logger.info("{} products in the store", productsInStorePagedQueryResponse.getResults().size());
                            productsInStorePagedQueryResponse.getResults().forEach(productsInStore -> {
                                logger.info(productsInStore.getProduct().getObj().getKey());
                                logger.info("MasterVariant Sku {}", productsInStore.getProduct().getObj().getMasterData().getCurrent().getMasterVariant().getSku());
                                productsInStore.getProduct().getObj().getMasterData().getCurrent().getVariants().forEach(variant ->
                                        logger.info("Variant Sku : {}", variant.getSku())
                                );
                            });
                        }
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();

            // TODO: Perform cart operations:
            //  TODO CREATE a new cart and add a product to it
            //



            // TODO: Perform cart operations:
            //  TODO Create an anonymous cart and add a product to it
            //
            storeService.getCurrentStore()
                    .thenComposeAsync(storeApiHttpResponse ->
                        cartService.createAnonymousCart(storeApiHttpResponse, "M0E20000000FHAO", 3L, supplyChannelKey, distChannelKey))
                    .thenAccept(cartApiHttpResponse ->
                        logger.info("cart created {}", cartApiHttpResponse.getBody().getId()))
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();

            // TODO UPDATE anonymousCartId variable above


            //  TODO: LOGIN customer or signup, if not found
            //
//            customerService.loginCustomer()
//                    .exceptionally(ex -> {
//                        logger.info("exception: {}", ex.getMessage());
//                        try {
//                            return customerService.createCustomer().get();
//                        } catch (Exception e) {throw new RuntimeException(e);}
//                    })
//                    .thenAccept(customerSignInResult -> logger.info("Current customer cart {}", customerSignInResult.getBody().getCart().getId()))
//                    .join();

            // TODO: ADD shipping address
            //
            customerService
                    .getCustomerByKey(customerKey)
                    .thenApply(ApiHttpResponse::getBody)
                    .thenApply(customer -> customer.getAddresses().stream()
                            .filter(address -> address.getId().equals(customer.getDefaultShippingAddressId()))
                            .findFirst()
                    )
                    .thenAccept(optionalAddress -> {
                        Address shippingAddress = optionalAddress.orElseGet(() -> AddressBuilder.of()
                                .firstName("First")
                                .lastName("Last")
                                .country("DE")
                                .key(customerKey + "-default")
                                .build()
                        );
                        try {
                            logger.info("Customer address added and set as default billing and shipping address:"
                                    + customerService.addAddressToCustomer(customerKey, shippingAddress)
                                    .get().getBody().getEmail()
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        cartService.getCartById(anonymousCartId)
                                .thenComposeAsync(cartApiHttpResponse ->
                                    cartService.addShippingAddress(cartApiHttpResponse, shippingAddress))
                                .thenAccept(cartApiHttpResponse ->
                                    logger.info("cart updated with shipping address {}", cartApiHttpResponse.getBody().getId()))
                                .exceptionally(throwable -> {
                                    logger.error("Exception: {}", throwable.getMessage());
                                    return null;
                                });
                    })
                    .exceptionally(ex -> {
                        logger.error("Error retrieving customer: {}", ex.getMessage());
                        return null;
                    }).join();

            // TODO ADD Payment to the cart
            cartService.getCartById(anonymousCartId)
//                    .thenComposeAsync(cartService::setShipping)
                    .thenComposeAsync(cartService::recalculate)
//                    .thenComposeAsync(cartApiHttpResponse ->
//                            paymentService.createPaymentAndAddToCart(
//                                    cartApiHttpResponse.getBody(),
//                                    "We_Do_Payments",
//                                    "CREDIT_CARD",
//                                    "we_pay_73636" + Math.random(),    // Must be unique.
//                                    "pay82626" + Math.random())                    // Must be unique.
//                    )
                    .thenAccept(cartApiHttpResponse -> logger.info("cart updated {}", cartApiHttpResponse.getBody().getId()))
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();

            // TODO Freeze cart
            cartService.getCartById(anonymousCartId)
                    .thenComposeAsync(cartService::freezeCart)
                    .thenAccept(cartApiHttpResponse -> logger.info("cart is now in frozen state {}", cartApiHttpResponse.getBody().getId()))
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();

            // TODO Unfreeze cart
            cartService.getCartById(anonymousCartId)
                    .thenComposeAsync(cartService::unfreezeCart)
                    .thenAccept(cartApiHttpResponse -> logger.info("cart is now in active state {}", cartApiHttpResponse.getBody().getId()))
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();

        }
    }
}
