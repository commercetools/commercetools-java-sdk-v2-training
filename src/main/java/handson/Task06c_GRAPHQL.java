package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.KeyReferenceBuilder;
import com.commercetools.graphql.api.GraphQL;
import com.commercetools.graphql.api.GraphQLData;
import com.commercetools.graphql.api.GraphQLRequest;
import com.commercetools.graphql.api.GraphQLResponse;
import com.commercetools.graphql.api.types.InStore;
import com.commercetools.graphql.api.types.KeyReference;
import com.commercetools.graphql.api.types.ProductAssignmentQueryResult;
import com.commercetools.graphql.api.types.ProductQueryResult;
import handson.impl.ApiPrefixHelper;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task06c_GRAPHQL {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            // TODO:
            //  Use the GraphQL playground to create a graphql query
            //

            GraphQLResponse<ProductQueryResult> responseEntity =
                    apiRoot
                            .graphql()
                            .query(GraphQL.products(q -> q.limit(3).sort(Collections.singletonList("masterData.current.name.en desc")))
                                    .projection(p -> p.total().results().id().masterData().current().name("en", null)))
                            .executeBlocking()
                            .getBody();

            logger.info("Total products: " + responseEntity.getData().getTotal());

            responseEntity.getData().getResults().forEach(result ->
                    logger.info("Id: " + result.getId() + "Name: " + result.getMasterData().getCurrent().getName()));


//            // TODO: GET the product assignments in the store using GraphQL
//
//            String query = "query($storeKey:KeyReferenceInput!) { " +
//                    "inStore(key:$storeKey) { " +
//                        "productSelectionAssignments { " +
//                            "results { " +
//                            "product { key skus } " +
//                            "productSelection { name(locale: \"en\") } " +
//                            "variantSelection { skus } " +
//                        "} " +
//                    "} " +
//                "} " +
//            "}";
//
//            // Build the variables map
//            Map<String, Object> variables = new HashMap<>();
//            variables.put("storeKey", getStoreKey(apiClientPrefix));
//
//            // Create the GraphQL request
//            GraphQLRequest<InStore> queryResultGraphQLRequest = GraphQL
//                    .query(query)
//                    .variables(graphQLVariablesMapBuilder -> graphQLVariablesMapBuilder.values(variables))
//                    .dataMapper(GraphQLData::getInStore)
//                    .build();
//
//            // Execute the query
//            ApiHttpResponse<ProductAssignmentQueryResult> response = apiRoot
//                    .graphql()
//                    .query(queryResultGraphQLRequest)
//                    .executeBlocking()
//                    .withBody(inStoreGraphQLResponse -> inStoreGraphQLResponse.getData().getProductSelectionAssignments());
//
//            // Log the product assignments
//            if (response.getBody() != null && response.getBody().getResults() != null) {
//                logger.info("Product Assignments: {}", response.getBody().getResults());
//            } else {
//                logger.warn("No product assignments found in the response.");
//            }
        }
    }
}
