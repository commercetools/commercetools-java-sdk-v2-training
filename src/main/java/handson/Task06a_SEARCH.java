package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryReference;
import com.commercetools.api.models.category.CategoryReferenceBuilder;
import com.commercetools.api.models.customer.CustomerSetCustomerGroupActionBuilder;
import com.commercetools.api.models.customer_group.CustomerGroupResourceIdentifierBuilder;
import com.commercetools.api.models.product.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Filter;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

public class Task06a_SEARCH {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey("mh-dev-admin.");
        final ApiRoot client = createApiClient("mh-dev-admin.");
        Logger logger = LoggerFactory.getLogger(Task06a_SEARCH.class.getName());

        Category plantSeedCategory = client
                .withProjectKey(projectKey)
                .categories()
                .withKey("plant-seeds")
                .get()
                .execute()
                .toCompletableFuture().get()
                .getBody();


        // example of other kind of filter

        // to get categoryReference
        CategoryReference plantSeedCategoryReference = CategoryReferenceBuilder.of().id(plantSeedCategory.getId()).build();
        // filter from product projection query response
//        ProductProjectionPagedQueryResponse productProjectionPagedQueryResponseWithFilter = client
//                .withProjectKey(projectKey)
//                .productProjections()
//                .get()
//                .withStaged(true)
//                .withWhere("categories=" + "\"" + plantSeedCategoryReference +  "\"")
//                .withLocaleProjection("DE")
//                .execute().get().getBody();

        // the effective filter from the search response
        // params found in the product projection search https://docs.commercetools.com/api/projects/products-search#search-productprojections
        ProductProjectionPagedSearchResponse productProjectionPagedSearchResponse = client
                .withProjectKey(projectKey)
                // TODO Get all products
                .productProjections()
                .search()
                .get()
                .withStaged(true)

                // TODO Restrict on category plant-seeds
                .withMarkMatchingVariants(true)

                // TODO Get all Facets for Enum size and Number weight_in_kg
                .withFacet("variants.attributes.size")
                .withFacet("variants.attributes.weight_in_kg")
                .withFilterQuery("categories.id:\"" + plantSeedCategoryReference.getId() + "\"")

                // TODO Give price range on products with no effect on facets
                .withFilter("variants.price.centAmount:range (10000 to 100000)")
                // TODO: with effect on facets
//                .withFilterFacets("variants.price.centAmount:range (10000 to 100000)")

                // TODO: Simulate click on facet box from attribute size
                .withFilterFacets("variants.attributes.size:10")
                .executeBlocking()
                .getBody();

        int size = productProjectionPagedSearchResponse.getResults().size();
        logger.info("Nr. of products: " + size);

        List<ProductProjection> result =  productProjectionPagedSearchResponse.getResults().subList(0, size);

        System.out.println("products searched: ");
        result.forEach((r) -> System.out.println(r.getKey()));
    }
}
