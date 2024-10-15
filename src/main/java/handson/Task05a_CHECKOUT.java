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

        Logger logger = LoggerFactory.getLogger("commercetools");

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        final String storeKey = getStoreKey(apiClientPrefix);

        CustomerService customerService = new CustomerService(client, storeKey);
        CartService cartService = new CartService(client, storeKey);
        OrderService orderService = new OrderService(client, storeKey);
        PaymentService paymentService = new PaymentService(client, storeKey);
        StoreService storeService = new StoreService(client, storeKey);

        // TODO: Fetch a channel if your inventory mode will not be NONE
        //
        final String supplyChannelKey = "sunrise-store-boston-1";
        final String distChannelKey = "sunrise-store-boston-1";
        final String initialStateKey = "mhOrderPacked2";
        final String customerKey = "ct-208557168810166";
        final String customerEmail = "nd@example.de";
        final String anonymousCartId = "992ceff9-6994-4e78-aa76-aa6ccaab7636";



//        // TODO: GET the products in the store
//        storeService.getProductsInCurrentStore()
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((productsInStorePagedQueryResponse, exception) -> {
//                    if (exception == null) {
//                        logger.info("{} products in the store", + productsInStorePagedQueryResponse.getResults().size());
//                        productsInStorePagedQueryResponse.getResults().forEach(productsInStore -> {
//                            logger.info(productsInStore.getProduct().getObj().getKey());
//                            logger.info("MasterVariant Sku {}", productsInStore.getProduct().getObj().getMasterData().getCurrent().getMasterVariant().getSku());
//                            productsInStore.getProduct().getObj().getMasterData().getCurrent().getVariants().forEach(variant ->
//                                logger.info("Variant Sku : {}", variant.getSku())
//                            );
//                        });
//                        return productsInStorePagedQueryResponse;
//                    }
//                    logger.error("Exception getting products for the store: " + exception.getMessage());
//                    return null;
//                }).thenRun(() -> client.close());

//        // TODO: Perform cart operations: add products to a new customer cart
//        //
//        customerService.getCustomerByKey(customerKey)
//                .thenCombineAsync(storeService.getCurrentStore(),((customerApiHttpResponse, storeApiHttpResponse) ->
//                        cartService.createCustomerCart(customerApiHttpResponse, storeApiHttpResponse,"M0E20000000FHA2", 1L, supplyChannelKey, distChannelKey)))
//                .get().thenApply(ApiHttpResponse::getBody)
//                .handle((cart, exception) -> {
//                        if (exception == null) {
//                            logger.info("cart created {}", cart.getId());
//                            return cart;
//                        }
//                        logger.error("Exception creating cart: " + exception.getMessage());
//                        return null;
//                }).thenRun(client::close);


//        // TODO: Perform cart operations: add products to a new anonymous cart
//        //
//        storeService.getCurrentStore()
//                .thenComposeAsync(storeApiHttpResponse ->
//                        cartService.createAnonymousCart(storeApiHttpResponse,"M0E20000000FHAO",3L, supplyChannelKey, distChannelKey))
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((cart, exception) -> {
//                    if (exception == null) {
//                        logger.info("cart created {}", cart.getId());
//                        return cart;
//                    }
//                    logger.error("Exception creating cart: " + exception.getMessage());
//                    return null;
//                }).thenRun(() -> client.close());

        //  TODO: LOGIN customer or signup, if not found
        //  TODO: add discount codes, perform a recalculation
        //  TODO: add payment
        //  TODO additionally: add custom line items, add shipping method
//        customerService.loginCustomer(
//                customerEmail,
//                "password",
//                anonymousCartId,
//                AnonymousCartSignInMode.USE_AS_NEW_ACTIVE_CUSTOMER_CART
//        )
//        .exceptionally(ex -> {
//                logger.info("exception: {}",ex.getMessage());
//                try {
//                        return  customerService.createCustomer(
//                                customerEmail,
//                                "password",
//                                anonymousCartId
//                                ).get();
//                } catch (Exception e) {
//                        throw new RuntimeException(e);
//                }
//        })
//        .thenAccept(customerSignInResult ->  {
//                logger.info("cart updated {}", customerSignInResult.getBody().getCart().getId());
//        }).thenRun(() -> client.close());

//        // TODO: ADD shipping address
//        //
//        customerService
//                .getCustomerByKey(customerKey)
//                .thenApply(ApiHttpResponse::getBody)
//                .thenApply(customer -> customer.getAddresses().stream()
//                            .filter(address -> address.getId().equals(customer.getDefaultShippingAddressId()))
//                            .findFirst()
//                )
//                .thenAccept(optionalAddress -> {
//                        Address shippingAddress = optionalAddress.orElseGet(() -> AddressBuilder.of()
//                                .firstName("First")
//                                .lastName("Tester")
//                                .country("DE")
//                                .key(customerKey + "-default")
//                                .build()
//                        );
//
//                        cartService.getCartById(anonymousCartId)
//                                .thenComposeAsync(cartApiHttpResponse -> cartService.addShippingAddress(cartApiHttpResponse, shippingAddress))
//                                .thenComposeAsync(cartService::setShipping)
//                                .thenComposeAsync(cartService::recalculate)
//                                .thenApply(ApiHttpResponse::getBody)
//                                .handle((cart, exception) -> {
//                                    if (exception == null) {
//                                        logger.info("cart updated with shipping address {}", cart.getId());
//                                        return cart;
//                                    }
//                                    else {
//                                        logger.error("Exception updating cart shipping info: " + exception.getMessage());
//                                        return null;
//                                    }
//                                }).thenRun(client::close);
//                })
//                .exceptionally(ex -> {
//                    logger.error("Error retrieving customer: {}", ex.getMessage());
//                    client.close(); // Close client if an error occurs
//                    return null;
//                });

//        // TODO ADD Payment to the cart
//        cartService.getCartById(anonymousCartId)
//                .thenComposeAsync(cartApiHttpResponse ->
//                        paymentService.createPaymentAndAddToCart(
//                            cartApiHttpResponse.getBody(),
//                            "We_Do_Payments",
//                            "CREDIT_CARD",
//                            "we_pay_73636" + Math.random(),    // Must be unique.
//                            "pay82626"+ Math.random())                    // Must be unique.
//                )
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((cart, exception) -> {
//                        if (exception == null) {
//                            logger.info("cart updated with payment {}", cart.getId());
//                            return cart;
//                        }
//                        logger.error("Exception adding payment to the cart: " + exception.getMessage());
//                        return null;
//                }).thenRun(() -> client.close());

//        // TODO Freeze cart
        cartService.getCartById(anonymousCartId)
                .thenComposeAsync(cartService::freezeCart)
                .thenApply(ApiHttpResponse::getBody)
                .handle((cart, exception) -> {
                        if (exception == null) {
                            logger.info("cart is now in frozen state {}", cart.getId());
                            return cart;
                        }
                        logger.error("Exception freezing the cart: " + exception.getMessage());
                        return null;
                }).thenRun(() -> client.close());

//        // TODO Unfreeze cart
//        cartService.getCartById(anonymousCartId)
//                .thenComposeAsync(cartService::unfreezeCart)
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((cart, exception) -> {
//                        if (exception == null) {
//                            logger.info("cart is now in active state {}", cart.getId());
//                            return cart;
//                        }
//                        logger.error("Exception unfreezing the cart: " + exception.getMessage());
//                        return null;
//                }).thenRun(() -> client.close());

//        // TODO Recalculate cart
//        cartService.getCartById(anonymousCartId)
//                .thenComposeAsync(cartService::recalculate)
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((cart, exception) -> {
//                    if (exception == null) {
//                        logger.info("cart has been recalculated {}", cart.getId());
//                        return cart;
//                    }
//                    logger.error("Exception recalculating the cart: " + exception.getMessage());
//                    return null;
//                }).thenRun(() -> client.close());
    }
}
