package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.product.ProductPagedQueryResponse;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task06b_PAGEDQUERY {


    // Solution in main fetches only one page
    //
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey("mh-dev-admin.");
        final ApiRoot client = createApiClient("mh-dev-admin.");

        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

        // UseCases
            // Fetching ALL products
            // Fetching ALL products of a certain type
            // Fetching ALL orders
            // Pagination of some entities BUT only ordered via id

            // Pagination is down to max 10.000
            final int PAGE_SIZE = 2;

            // Instead of asking for next page, ask for elements being greater than this id

            // TODO in class:
            // Give last id, start with slightly modified first id OR: do not use id when fetching first page
            // Give product type id
            //
            String lastId = "642b9b40-5dae-4051-8647-d22e9ca98044";
            String productTypeId = "7f30329c-bfaa-4c75-97bf-58caf1103900";

           //  link to give to our customers https://docs.commercetools.com/api/predicates/query

        ProductPagedQueryResponse productPagedQueryResponse =
                client.withProjectKey(projectKey)
                        .products()
                        .get()

                        .withWhere("productType(id = :productTypeId)")
                        .addQueryParam("var.productTypeId", productTypeId)

                        // Important, internally we use id > $lastId, it will not work without this line
                        .withSort("id asc")

                        // Limit the size per page
                        .withLimit(PAGE_SIZE)

                        // use this for following pages
                        .withWhere("id > :lastId")
                        .addQueryParam("var.lastId", lastId)

                        // always use this
                        .withWithTotal(false)

                        .execute()
                        .toCompletableFuture().get()
                        .getBody();

        // Print results
            logger.info("Found product size: " + productPagedQueryResponse.getResults().size());
            productPagedQueryResponse.getResults().forEach(
                    product -> logger.info("Product: " + product.getId())
            );
        System.exit(0);

    }
}




/* Old generic solution for fetching all products
//
    public CompletionStage<List<Product>> findNext(final ProductQuery seedQuery, final ProductQuery query, final List<Product> products, final int PAGE_SIZE) {
        final CompletionStage<PagedQueryResult<Product>> pageResult = client.execute(query);
        return pageResult.thenCompose(page -> {
            final List<Product> results = page.getResults();
            products.addAll(results);
            final boolean isLastQueryPage = results.size() < PAGE_SIZE;
            if (isLastQueryPage) {
                return CompletableFuture.completedFuture(products);
            } else {
                final String lastId = getIdForNextQuery(page);
                return findNext(seedQuery, seedQuery
                        .plusPredicates(m -> m.id().isGreaterThan(lastId)), products, PAGE_SIZE);
            }
        });
    }

    private <T extends Identifiable<T>> String getIdForNextQuery(final PagedResult<T> pagedResult) {
        final List<T> results = pagedResult.getResults();
        final int indexLastElement = results.size() - 1;
        return results.get(indexLastElement).getId();
    }
*/

