package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product.ProductPagedQueryResponse;
import handson.impl.ApiPrefixHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Task06b_PAGEDQUERY {


    // Solution in main fetches only one page
    //
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        Logger logger = LoggerFactory.getLogger(Task06b_PAGEDQUERY.class.getName());

        // UseCases
        // Fetching ALL products
        // Fetching ALL products of a certain type
        // Fetching ALL orders
        // Pagination of some entities BUT only ordered via id

        // Pagination is down to max 10.000
        final int PAGE_SIZE = 2;
        Boolean lastPage = false;

        // Instead of using offset to get a page, ask for elements being greater than the id of the first
        // product in your project

        String lastId = client
                .products()
                .get()
                .withSort("id asc")
                .withLimit(1)
                .execute()
                .toCompletableFuture().get()
                .getBody().getResults().get(0).getId();

        // Get the product type Id, to be used in where

        String productTypeId = client
                .productTypes()
                .withKey("plant-seeds-product-type")
                .get()
                .execute()
                .toCompletableFuture().get()
                .getBody().getId();

        //  link to give to our customers https://docs.commercetools.com/api/predicates/query

        while(!lastPage) {
            ProductPagedQueryResponse productPagedQueryResponse =
                    client
                            .products()
                            .get()

                            // Important, internally we use id > $lastId, it will not work without this line
                            .withSort("id asc")

                            .withWhere("productType(id = :productTypeId)")
                            .addWhere("id > :lastId")
                            .withPredicateVar("productTypeId", productTypeId)
                            .addPredicateVar("lastId", lastId)


                            // Limit the size per page
                            .withLimit(PAGE_SIZE)

                            // always use this
                            .withWithTotal(false)

                            .execute()
                            .toCompletableFuture().get()
                            .getBody();

            // Print results
            int size = productPagedQueryResponse.getResults().size();

            if ( size != 0) {
                logger.info("////////////////////////////////");
                logger.info("Found products: " + size);
                productPagedQueryResponse.getResults().forEach(
                        product -> logger.info("Product: " + product.getId())
                );
                lastId = productPagedQueryResponse.getResults().get(size - 1).getId();
            }
            lastPage = !(size == PAGE_SIZE);
        }
        client.close();
    }
}


