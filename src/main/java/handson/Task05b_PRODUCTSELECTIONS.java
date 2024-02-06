package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product_selection.AssignedProductReference;
import com.commercetools.api.models.product_selection.ProductSelection;
import handson.impl.ApiPrefixHelper;
import handson.impl.ProductSelectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


/**
 *
 */
public class Task05b_PRODUCTSELECTIONS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger("commercetools");
        final String globalApiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot client = createApiClient(globalApiClientPrefix);

        final ProductSelectionService productSelectionService = new ProductSelectionService(client);

        // TODO: In Merchant Center, create product selection and add a product to the product selection.
        // Update the keys below

        final String productSelectionKey = "";
        final String storeKey = "";

        // TODO: Get a store and assign the product selection to the store and activate it

        logger.info("Product selection added to the store: "
                + ""
        );


        // TODO Get products in a product selection

        List<AssignedProductReference> assignedProductReferences = null;
//        assignedProductReferences.forEach(assignedProductReference -> logger.info(assignedProductReference.getProduct().getObj().getKey()));

        client.close();
    }
}
