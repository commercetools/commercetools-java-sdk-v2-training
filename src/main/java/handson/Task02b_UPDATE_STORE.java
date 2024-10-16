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
        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final String storeKey = getStoreKey(apiClientPrefix);
            StoreService storeService = new StoreService(client, storeKey);

            final String productSelectionKey = "product-selection";

            // TODO: Assign Product Selection to your store
            //
            // storeService.addProductSelectionToCurrentStore(productSelectionKey)


            // TODO: GET the products in the store
            storeService.getProductsInCurrentStore()
                    .thenApply(ApiHttpResponse::getBody)
                    .thenAccept(productsInStorePagedQueryResponse -> {
                            logger.info("{} products in the store", productsInStorePagedQueryResponse.getResults().size());
                            productsInStorePagedQueryResponse.getResults().forEach(productsInStore ->
                                    logger.info(productsInStore.getProduct().getObj().getKey())
                            );
                        }
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    }).join();
        }
    }
}

