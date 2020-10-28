package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryReference;
import com.commercetools.api.models.category.CategoryReferenceBuilder;
import com.commercetools.api.models.product.*;
import com.commercetools.api.product.FacetResultsAccessor;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

public class Task06a_SEARCH {

    public static void main(String[] args) throws Exception {

        // TODO:
        //  Check your prefix
        //
        final String apiClientPrefix = "mh-dev-admin.";

        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task06a_SEARCH.class.getName());

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {

            Category plantSeedCategory = client
                    .withProjectKey(projectKey)
                    .categories()
                    .withKey("plant-seeds")
                    .get()
                    .execute()
                    .toCompletableFuture().get()
                    .getBody();

            // to get categoryReference
            CategoryReference plantSeedCategoryReference = CategoryReferenceBuilder.of().id(plantSeedCategory.getId()).build();

            // filter from product projection query response

            // the effective filter from the search response
            // params found in the product projection search https://docs.commercetools.com/api/projects/products-search#search-productprojections
            ProductProjectionPagedSearchResponse productProjectionPagedSearchResponse = client
                    .withProjectKey(projectKey)
                    // TODO Get all products
                    .productProjections()
                    .search()
                    .get()
                    .withStaged(false)

                    // TODO Restrict on category plant-seeds
                    .withMarkMatchingVariants(true)
                    .withFilterQuery("categories.id:\"" + plantSeedCategoryReference.getId() + "\"")

                    // TODO Get all Facets for Enum size and Number weight_in_kg
                    .withFacet("variants.attributes.size")
                    .withFacet("variants.attributes.weight_in_kg")

                    // TODO Give price range on products with no effect on facets
                    // .withFilter("variants.price.centAmount:range (100 to 100000)")
                    // TODO: with effect on facets
                    // .withFilterQuery("variants.price.centAmount:range (100 to 100000)")

                    // TODO: Simulate click on facet box from attribute size
                    // .withFilterFacets("variants.attributes.size.label:\"box\"")
                    .executeBlocking()
                    .getBody();

            int size = productProjectionPagedSearchResponse.getResults().size();
            logger.info("Nr. of products: " + size);

            List<ProductProjection> result =  productProjectionPagedSearchResponse.getResults().subList(0, size);


            logger.info("Facets: " + productProjectionPagedSearchResponse.getFacets().values().size());
            logger.info("Facet Values" + productProjectionPagedSearchResponse.getFacets().values());
            Map<String, FacetResult> facetResults= productProjectionPagedSearchResponse.getFacets().withFacetResults(FacetResultsAccessor::new).facets();
            facetResults.forEach((s, facet) -> System.out.println(s + " " + facet.toString()));
            logger.info("Facets: " + productProjectionPagedSearchResponse.getFacets().toString());

            logger.info("Facet Weight: ");
            FacetResult weightRangeFacetResult = productProjectionPagedSearchResponse.getFacets().withFacetResults(FacetResultsAccessor::asFacetResultMap).get("variants.attributes.weight_in_kg");
            if (weightRangeFacetResult instanceof RangeFacetResult) {
                logger.info("Weight: Nr. of Ranges: {}", ((RangeFacetResult)weightRangeFacetResult).getRanges().size());
                logger.info("Weight: Ranges: {}", ((RangeFacetResult)weightRangeFacetResult).getRanges().toString());
            }
            logger.info("Facet Size: ");
            FacetResult sizeBoxFacetResult = productProjectionPagedSearchResponse.getFacets().withFacetResults(FacetResultsAccessor::asFacetResultMap).get("variants.attributes.size");
            if (sizeBoxFacetResult instanceof TermFacetResult) {
                logger.info("Size Box Facet Result: {}", ((TermFacetResult)sizeBoxFacetResult).getTerms().stream().map(facetResultTerm -> facetResultTerm.getTerm().toString()).collect(Collectors.joining(",")));
            }

            System.out.println("products searched: ");
            result.forEach((r) -> System.out.println(r.getKey()));

        }
    }
}
