package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.product.FacetResults;
import com.commercetools.api.models.product.ProductProjectionPagedSearchResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static handson.impl.ClientService.createApiClient;

public class Task06a_SEARCH {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = "training-011-avensia-test";
        final ApiRoot client = createApiClient("mh-dev-admin.");
        Logger logger = Logger.getLogger(Task04b_CHECKOUT.class.getName());

        Category plantSeedCategory = client
                .withProjectKey(projectKey)
                .categories()
                .withKey("plant-seeds")
                .get()
                .execute()
                .toCompletableFuture().get()
                .getBody();

            // final PagedSearchResult<ProductProjection> productProjectionPagedSearchResult_priceRange =


        ProductProjectionPagedSearchResponse productProjectionPagedSearchResponse = client
                .withProjectKey(projectKey)
                // TODO Get all products
                .productProjections()
                .search()
                .get()
                .withStaged(true)

                // TODO Restrict on category plant-seeds
                .withMarkMatchingVariants(true)
                //     searchModel -> searchModel .categories().id().is(plantSeedCategory.getId()))

                // TODO Get all Facets for Enum size and Number weight_in_kg
                // .plusFacets(searchModel -> searchModel.allVariants().attribute().ofEnum("size").label().allTerms())
                // .plusFacets(searchModel -> searchModel.allVariants().attribute().ofNumber("weight_in_kg").allRanges())
                .withFacet("size")
                .withFacet("weight_in_kg")


                // TODO Give price range on products with no effect on facets
                // .with....
                // .plusResultFilters(searchModel -> searchModel.allVariants().price().centAmount().isBetween(100L, 10000L))
                // TODO: with effect on facets
                // .withFilterQuery()
                // .plusQueryFilters(searchModel -> searchModel.allVariants().price().centAmount().isBetween(100L, 10000L))

                // TODO: Simulate click on facet box from attribute size
                // .withFilterFacets()
                // .plusFacetFilters(searchResult -> searchResult.allVariants().attribute().ofEnum("size").label().is("box"))

                .execute()
                .toCompletableFuture().get()
                .getBody();


        logger.info("Nr. of products: " + productProjectionPagedSearchResponse.getResults().size());

        // LOG.info("Facets: " + productProjectionPagedSearchResult_priceRange.getFacetsResults().size());
        // LOG.info("Facet Values" + productProjectionPagedSearchResult_priceRange.getFacetsResults().values());
        FacetResults facetResults = productProjectionPagedSearchResponse.getFacets();
        facetResults.values().forEach((s, jsonNode) -> System.out.println(s + " " + jsonNode.textValue()));
        logger.info("Facets: " + productProjectionPagedSearchResponse.getFacets().toString());

        /*
            LOG.info("Facet Weight: ");
            RangeFacetResult weightRangeFacetResult = (RangeFacetResult) productProjectionPagedSearchResult_priceRange.getFacetResult("variants.attributes.weight_in_kg");
            if (weightRangeFacetResult != null) {
                LOG.info("Weight: Nr. of Ranges: {}", weightRangeFacetResult.getRanges().size());
                LOG.info("Weight: Ranges: {}", weightRangeFacetResult.getRanges().toString());
            }

            LOG.info("Facet Size: ");
            TermFacetResult sizeBoxFacetResult = productProjectionPagedSearchResult_priceRange.getTermFacetResult("variants.attributes.size");
            if (sizeBoxFacetResult != null) {
                LOG.info("Size Box Facet Result: {}", sizeBoxFacetResult.toString());
            }
        */



    }
}
