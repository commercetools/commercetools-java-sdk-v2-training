package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task03a_CHECKOUT {

    private static final Logger log = LoggerFactory.getLogger(Task03a_CHECKOUT.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String supplyChannelKey = "boston-store-channel";
        final String distChannelKey = "boston-store-channel";
        final String cartId = "";

        final String apiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");
            final String storeKey = getStoreKey(apiClientPrefix);

            CartService cartService = new CartService(apiRoot, storeKey);
            StoreService storeService = new StoreService(apiRoot, storeKey);

            // TODO: GET the products in the store
            //
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


//            // TODO: Perform cart operations:
//            //  TODO Create an anonymous cart and add a product to it
//            //
//            storeService.getCurrentStore()
//                    .thenComposeAsync(storeApiHttpResponse ->
//                            cartService.createAnonymousCart(storeApiHttpResponse, "AAR-34", 3L, supplyChannelKey, distChannelKey))
//                    .thenAccept(cartApiHttpResponse -> logger.info("cart created {}", cartApiHttpResponse.getBody().getId()))
//                    .exceptionally(throwable -> {
//                        logger.error("Exception: {}", throwable.getMessage());
//                        return null;
//                    }).join();
//            // TODO UPDATE cartId variable above

//            // TODO ADD a shipping address to the cart
//            //

//            // TODO Recalculate cart
//            //

//            // TODO Freeze cart
//            //


//            // TODO Unfreeze cart
//            //
//            cartService.getCartById(cartId)
//                    .thenComposeAsync(cartService::unfreezeCart)
//                    .thenAccept(cartApiHttpResponse -> logger.info("cart is now in active state {}", cartApiHttpResponse.getBody().getId()))
//                    .exceptionally(throwable -> {
//                        logger.error("Exception: {}", throwable.getMessage());
//                        return null;
//                    }).join();

        }
    }
}
