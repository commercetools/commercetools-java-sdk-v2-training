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
        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        Logger logger = LoggerFactory.getLogger("commercetools");

        final String storeKey = getStoreKey(apiClientPrefix);

        // TODO: CREATE a Product Selection
        //
        final String productSelectionKey = "nd-boston-selection1";
        Map<String, String> psName = new HashMap<String, String>() {
            {
                put("DE", "nd boston selection 1");
                put("EN", "nd boston selection 1");
            }
        };


        final ProductSelectionService productSelectionService = new ProductSelectionService(client);

        productSelectionService.createProductSelection(productSelectionKey, psName)
                .thenApply(ApiHttpResponse::getBody)
                .handle((productSelection, exception) -> {
                    if (exception == null) {
                        logger.info("Product Selection created: " + productSelection.getId()); return productSelection;
                    };
                    logger.error("Exception: " + exception.getMessage());
                    return null;
                }
        );

//        // TODO: ADD Products to Product Selection
//        //
//        productSelectionService.getProductSelectionByKey(productSelectionKey)
//                .thenComposeAsync(productSelectionApiHttpResponse ->
//                        productSelectionService.addProductToProductSelection(productSelectionApiHttpResponse, "86935"))
//                .thenApply(ApiHttpResponse::getBody)
//                .handle((productSelection, exception) -> {
//                        if (exception == null) {
//                            logger.info("Product Selection updated: " + productSelection.getId()); return productSelection;
//                        };
//                        logger.error("Exception while adding product: " + exception.getMessage());
//                        return null;
//                });

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
