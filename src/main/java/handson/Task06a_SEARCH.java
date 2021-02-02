package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryReference;
import com.commercetools.api.models.category.CategoryReferenceBuilder;
import com.commercetools.api.models.product.*;
import com.commercetools.api.product.FacetResultsAccessor;
import handson.impl.ClientService;
import handson.impl.PrefixHelper;
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


        final String apiClientPrefix = PrefixHelper.getDevApiClientPrefix();

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
            CategoryReference plantSeedCategoryReference =
                    CategoryReferenceBuilder.of()
                            .id(plantSeedCategory.getId())
                            .build();

            // filter from product projection query response

            // the effective filter from the search response
            // params found in the product projection search https://docs.commercetools.com/api/projects/products-search#search-productprojections
            ProductProjectionPagedSearchResponse productProjectionPagedSearchResponse = null;






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
