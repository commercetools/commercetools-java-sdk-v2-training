package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryReference;
import com.commercetools.api.models.category.CategoryReferenceBuilder;
import com.commercetools.api.models.product.*;
import handson.impl.ApiPrefixHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static handson.impl.ClientService.createApiClient;

public class Task06a_SEARCH {

    public static void main(String[] args) throws Exception {

// TODO UPDATE: Product projection in Store
        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            Category seedCategory = apiRoot
                    .categories()
                    .withKey("plant-seeds")
                    .get()
                    .execute()
                    .get()
                    .getBody();

            // to get categoryReference
            CategoryReference seedCategoryReference =
                    CategoryReferenceBuilder.of()
                            .id(seedCategory.getId())
                            .build();

            // filter from product projection query response

            // the effective filter from the search response
            // params found in the product projection search https://docs.commercetools.com/api/projects/products-search#search-productprojections
            ProductProjectionPagedSearchResponse productProjectionPagedSearchResponse = apiRoot
                    // TODO Get all products
                    .productProjections()
                    .search()
                    .get()
                    .withStaged(false)

                    // TODO Restrict on category plant-seeds
                    .withMarkMatchingVariants(true)
                    .withFilterQuery("categories.id:\"" + seedCategoryReference.getId() + "\"")

                    // TODO Get all Facets for Enum size and Number weight_in_kg

                    .withFacet("variants.attributes.packing")
                    .addFacet("variants.attributes.weight_in_kg:range (0 to 1), (1 to 5), (5 to 20)")


                    // TODO Give price range on products with no effect on facets
                    // .withFilter("variants.price.centAmount:range (100 to 100000)")
                    // TODO: with effect on facets
                    //                 .addFilterQuery("variants.price.centAmount:range (100 to 100000)")

                    // TODO: Simulate click on facet box from attribute size
                    //.withFilterFacets("variants.attributes.size:\"box\"")
                    .executeBlocking()
                    .getBody();


            int size = productProjectionPagedSearchResponse.getResults().size();
            logger.info("No. of products: " + size);
            List<ProductProjection> result = productProjectionPagedSearchResponse.getResults().subList(0, size);
            System.out.println("products searched: ");
            result.forEach((r) -> System.out.println(r.getKey()));

            logger.info("Facets: " + productProjectionPagedSearchResponse.getFacets().values().size());
            logger.info("Facet Values" + productProjectionPagedSearchResponse.getFacets().values().toString());
            Map<String, FacetResult> facetResults = productProjectionPagedSearchResponse.getFacets().withFacetResults(FacetResultsAccessor::new).facets();
            facetResults.forEach((s, facet) -> System.out.println(s + " " + facet.toString()));
            logger.info("Facets: " + productProjectionPagedSearchResponse.getFacets().toString());


            logger.info("Facet Weight (kg): ");
            FacetResult weightRangeFacetResult = productProjectionPagedSearchResponse.getFacets().values().get("variants.attributes.weight_in_kg");
            if (weightRangeFacetResult instanceof RangeFacetResult) {
                logger.info("No. of Weight Terms: {}", ((RangeFacetResult) weightRangeFacetResult).getRanges().size());
                logger.info("Weight Terms: {}", ((RangeFacetResult) weightRangeFacetResult).getRanges().stream().map(facetResultRange -> facetResultRange.getFrom().intValue() + " to " + facetResultRange.getTo().intValue() + " - " + facetResultRange.getCount()).collect(Collectors.toList()));
            }
            logger.info("Facet Packing Type: ");
            FacetResult sizeBoxFacetResult = productProjectionPagedSearchResponse.getFacets().values().get("variants.attributes.packing");
            if (sizeBoxFacetResult instanceof TermFacetResult) {
                logger.info("No. of Packing Terms: {}", ((TermFacetResult) sizeBoxFacetResult).getTerms().size());
                logger.info("Packing Type Facet Result: {}", ((TermFacetResult) sizeBoxFacetResult).getTerms().stream().map(facetResultTerm -> facetResultTerm.getTerm().toString() + " - " + facetResultTerm.getCount()).collect(Collectors.joining(", ")));
            }

        }
    }
}
