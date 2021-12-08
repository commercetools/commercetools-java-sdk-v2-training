package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.api.models.graph_ql.GraphQLRequestBuilder;
import handson.graphql.ProductCustomerQuery;
import handson.impl.ApiPrefixHelper;
import io.aexp.nodes.graphql.*;
import io.vrap.rmf.base.client.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.*;


public class Task06c_GRAPHQL_Nodes {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final String projectKey = getProjectKey(apiClientPrefix);
        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());


        // TODO:
        //  Use the GraphQL playground to create a graphql query
        //

        logger.info("GraphQl : " +
                client
                        .graphql()
                        .post(
                                GraphQLRequestBuilder.of()
                                        .query(
                                                "{ products { total }}"
                                        )
                                        .build()
                        )
                        .execute()
                        .toCompletableFuture().get()
                        .getBody()
                        .getData()
        );
        client.close();


        // TODO:
        //  Fetch a token, then inspect the following code
        //
        final AuthenticationToken authenticationToken = getTokenForClientCredentialsFlow(apiClientPrefix);
        logger.info("\nToken fetched : " + authenticationToken.getAccessToken());

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + authenticationToken.getAccessToken());
        // replace in Java 9 with .headers(Map.of("Authorization", "Bearer " + token))

        GraphQLResponseEntity<ProductCustomerQuery> responseEntity =
                    new GraphQLTemplate()
                            .query(
                                    GraphQLRequestEntity.Builder()
                                            .url(ServiceRegion.GCP_EUROPE_WEST1.getApiUrl() + "/" + projectKey + "/graphql")
                                            .headers(headers)
                                            .request(ProductCustomerQuery.class)
                                            .arguments(new Arguments("products",
                                                    new Argument<>("limit", 3),
                                                    new Argument<>("sort", "masterData.current.name.en desc")
                                            ))
                                            .build(),
                                    ProductCustomerQuery.class
                            );
        logger.info("Total products: " + responseEntity.getResponse().getProducts().getTotal());
        responseEntity.getResponse().getProducts().getResults().forEach(result ->
                    logger.info("Id: " + result.getId() + "Name: " + result.getMasterData().getCurrent().getName()));
        logger.info("Total customers: " + responseEntity.getResponse().getCustomers().getTotal());
    }
}
