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

        final String supplyChannelKey = "sunrise-store-boston-1";
        final String distChannelKey = "sunrise-store-boston-1";
        final String initialStateKey = "OrderPacked";
        final String customerKey = "ct-208557168810166";
        final String customerEmail = "ct@example.de";
        final String anonymousCartId = "992ceff9-6994-4e78-aa76-aa6ccaab7636";

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");
            final String storeKey = getStoreKey(apiClientPrefix);

            CustomerService customerService = new CustomerService(client, storeKey);
            CartService cartService = new CartService(client, storeKey);
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

            // TODO: Perform cart operations: add products to a new customer cart
            //
            customerService.getCustomerByKey(customerKey)
                    .thenCombineAsync(storeService.getCurrentStore(), ((customerApiHttpResponse, storeApiHttpResponse) ->
                            cartService.createCustomerCart(customerApiHttpResponse, storeApiHttpResponse, "M0E20000000FHA2", 1L, supplyChannelKey, distChannelKey)))
                    .get()
                    .thenAccept(cartApiHttpResponse -> logger.info("cart created {}", cartApiHttpResponse.getBody().getId()))
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();


            // TODO: Perform cart operations: add products to a new anonymous cart
            //
            storeService.getCurrentStore()
                    .thenComposeAsync(storeApiHttpResponse ->
                            cartService.createAnonymousCart(storeApiHttpResponse, "M0E20000000FHAO", 3L, supplyChannelKey, distChannelKey))
                    .thenAccept(cartApiHttpResponse -> logger.info("cart created {}", cartApiHttpResponse.getBody().getId()))
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

            // TODO Recalculate cart
            cartService.getCartById(anonymousCartId)
                    .thenComposeAsync(cartService::recalculate)
                    .thenAccept(cartApiHttpResponse -> logger.info("cart has been recalculated {}", cartApiHttpResponse.getBody().getId()))
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();
        }
    }
}
