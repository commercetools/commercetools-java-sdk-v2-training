package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.ApiPrefixHelper;
import handson.impl.StoreService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;

public class Task02b_UPDATE_STORE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        final String storeKey = getStoreKey(apiClientPrefix);
        StoreService storeService = new StoreService(client, storeKey);

        // TODO: Assign Product Selection to your store
        //
        final String productSelectionKey = "nd-boston-selection1";
        storeService.addProductSelectionToCurrentStore(productSelectionKey)
        .thenApply(ApiHttpResponse::getBody)
        .handle((store, exception) -> {
                if (exception == null) {
                    logger.info("Store Updated: " + store.getId());
                    return store;
                };
                logger.error("Exception while updating store: " + exception.getMessage());
                return null;
        }).thenRun(() -> client.close());


        // TODO: GET the products in the store
//        storeService.getProductsInCurrentStore()
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((productsInStorePagedQueryResponse, exception) -> {
//                    if (exception == null) {
//                        logger.info("{} products in the store", + productsInStorePagedQueryResponse.getResults().size());
//                        productsInStorePagedQueryResponse.getResults().forEach(productsInStore ->
//                                logger.info(productsInStore.getProduct().getObj().getKey())
//                        );
//                        return productsInStorePagedQueryResponse;
//                    }
//                    logger.error("Exception: " + exception.getMessage());
//                    return null;
//                }).thenRun(() -> client.close());

    }

}

