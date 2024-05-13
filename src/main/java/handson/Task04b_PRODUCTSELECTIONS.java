package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.ApiPrefixHelper;
import handson.impl.ProductSelectionService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;

public class Task04b_PRODUCTSELECTIONS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger("commercetools");
        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        final String storeKey = getStoreKey(apiClientPrefix);

        final ProductSelectionService productSelectionService = new ProductSelectionService(client);

        // TODO: In Merchant Center, create a product selection and add a product to the product selection.
        // Update the key below

        final String productSelectionKey = "good-store-selection";

        // TODO: Assign the product selection to the store
        productSelectionService.addProductSelectionToStore(storeKey, productSelectionKey)
                .thenApply(ApiHttpResponse::getBody)
                .handle((store, exception) -> {
                    if (exception == null) {
                        logger.info("{} product selections assigned to the store", + store.getProductSelections().size());
                        return store;
                    }
                    logger.error("Exception: " + exception.getMessage());
                    return null;
                }).thenRun(() -> client.close());

//        // TODO: GET the products in the store
//        productSelectionService.getProductsInStore(storeKey)
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


//        // TODO Get products in a product selection
//        productSelectionService.getProductsInProductSelection(productSelectionKey)
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((productReferences, exception) -> {
//                    if (exception == null) {
//                        productReferences.getResults().forEach(assignedProductReference ->
//                            logger.info(assignedProductReference.getProduct().getObj().getKey())
//                        );
//                        return null;
//                    }
//                    logger.error("Exception: " + exception.getMessage());
//                    return null;
//                }).thenRun(() -> client.close());
    }
}
