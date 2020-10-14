package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.graph_ql.GraphQLRequestBuilder;
import com.commercetools.api.models.graph_ql.GraphQLResponse;
import com.commercetools.api.models.graph_ql.GraphQLResponseBuilder;
import handson.Task04b_CHECKOUT;
import handson.graphql.ProductCustomerQuery;
import handson.impl.ThirdPartyClientService;
import io.aexp.nodes.graphql.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.json.*;

import static handson.impl.ClientService.createApiClient;


public class Task06c_GRAPHQL_Nodes {


    public void fetchProductTotalsViaGraphQLandNodes(String token, String projectID) {

        try {
           Map<String, String> headers = new HashMap<>();
           headers.put("Authorization", "Bearer " + token);
           // replace in Java 9 with .headers(Map.of("Authorization", "Bearer " + token))

           GraphQLResponseEntity<ProductCustomerQuery> responseEntity =
                   new GraphQLTemplate()
                           .query(
                                   GraphQLRequestEntity.Builder()
                                        .url("https://api.europe-west1.gcp.commercetools.com/" + projectID + "/graphql")
                                        .headers(headers)
                                        .request(ProductCustomerQuery.class)
                                           .arguments(new Arguments("products",
                                                   new Argument("limit", 2),
                                                   new Argument("sort", "masterData.current.name.en desc")
                                           ))
                                        .build(),
                                   ProductCustomerQuery.class
                           );
            System.out.println("Total products: " + responseEntity.getResponse().getProducts().getTotal());
           responseEntity.getResponse().getProducts().getResults().forEach(result ->
                   System.out.println("Id: " + result.getId() + "Name: " + result.getMasterData().getCurrent().getName()));
            System.out.println("Total customers: " + responseEntity.getResponse().getCustomers().getTotal());

       }
       catch (MalformedURLException e) {
           System.out.println(e.toString());
       }
    }



    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = "training-011-avensia-test";
        final ApiRoot client = createApiClient("mh-dev-admin.");

        Logger logger = Logger.getLogger(Task04b_CHECKOUT.class.getName());

        JsonObject simpleGraphQLQuery = Json.createObjectBuilder()
                .add("ProductQuery",
                            Json.createObjectBuilder()
                                    .add("products", "{ total }")
                                    .build()
                )
                .build();

        final GraphQLResponse graphQLResponse = client.withProjectKey(projectKey)
                .graphql()
                .post(
                        GraphQLRequestBuilder.of()
                                .query(
                                        "{ products { total } }"
                                )
                                .build()
                )
                .execute()
                .toCompletableFuture().get()
                .getBody();

        logger.info("GraphQl : " + graphQLResponse.getData().toString());


        // Solution using Nodes
        //
//        Task06c_GRAPHQL_Nodes task06C_graphqlNodes = new Task06c_GRAPHQL_Nodes();
//        ThirdPartyClientService thirdPartyClientService = new ThirdPartyClientService();
//        String token = thirdPartyClientService.createClientAndFetchToken("UC6k6y0EFoloW6bizT5PskhW", "wj3tWTnXY1Y4I__DKeoaKpeUBujm27mI", projectKey);
//        task06C_graphqlNodes.fetchProductTotalsViaGraphQLandNodes(token, projectKey);

    }
}
