package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.ProductSelectionService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task02a_CREATE_PRODUCTSELECTION {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {

            Logger logger = LoggerFactory.getLogger("commercetools");

            final String storeKey = getStoreKey(apiClientPrefix);

            // TODO: CREATE a Product Selection
            //
            final String productSelectionKey = "nd-boston-selection";
            Map<String, String> psName = new HashMap<String, String>() {
                {
                    put("DE", "nd boston selection");
                    put("EN", "nd boston selection");
                }
            };

            final ProductSelectionService productSelectionService = new ProductSelectionService(client);

            productSelectionService.createProductSelection(productSelectionKey, psName)
                    .thenAccept(productSelectionApiHttpResponse ->
                            logger.info("Product Selection created: "
                                    + productSelectionApiHttpResponse.getBody().getId())
                    )
                    .exceptionally(throwable -> {
                        logger.error("Exception: {}", throwable.getMessage());
                        return null;
                    });

//        // TODO: ADD Products to Product Selection
//        //
//        productSelectionService.getProductSelectionByKey(productSelectionKey)
//                .thenComposeAsync(productSelectionApiHttpResponse ->
//                        productSelectionService.addProductToProductSelection(productSelectionApiHttpResponse, "86935"))
//                .thenAccept(productSelectionApiHttpResponse ->
//                        logger.info("Product Selection updated: " + productSelectionApiHttpResponse.getBody().getId())
//                )
//                .exceptionally(throwable -> {
//                    logger.error("Exception: {}", throwable.getMessage());
//                    return null;
//                });

//        // TODO Get products in a product selection
//        productSelectionService.getProductsInProductSelection(productSelectionKey)
//                .thenApply(ApiHttpResponse::getBody)
//                .thenAccept(productReferences -> {
//                        productReferences.getResults().forEach(assignedProductReference ->
//                            logger.info(assignedProductReference.getProduct().getObj().getKey())
//                        );
//                    }
//                )
//                .exceptionally(throwable -> {
//                    logger.error("Exception: {}", throwable.getMessage());
//                    return null;
//                }).join();
        }
    }
}
