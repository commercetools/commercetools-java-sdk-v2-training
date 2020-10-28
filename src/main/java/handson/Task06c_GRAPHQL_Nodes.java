package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.api.models.graph_ql.GraphQLRequestBuilder;
import com.commercetools.api.models.graph_ql.GraphQLResponse;
import com.commercetools.api.models.graph_ql.GraphQLResponseBuilder;
import handson.Task04b_CHECKOUT;
import handson.graphql.ProductCustomerQuery;
import handson.impl.ClientService;
import handson.impl.ThirdPartyClientService;
import io.aexp.nodes.graphql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import javax.json.*;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task06c_GRAPHQL_Nodes {


    public static final String MH_DEV_ADMIN = "mh-dev-admin.";

    public void fetchProductTotalsViaGraphQLandNodes(String token, String projectKey) {

        try {
           Map<String, String> headers = new HashMap<>();
           headers.put("Authorization", "Bearer " + token);
           // replace in Java 9 with .headers(Map.of("Authorization", "Bearer " + token))

           GraphQLResponseEntity<ProductCustomerQuery> responseEntity =
                   new GraphQLTemplate()
                           .query(
                                   GraphQLRequestEntity.Builder()
                                        .url(ServiceRegion.GCP_EUROPE_WEST1.getApiUrl() + "/" + projectKey + "/graphql")
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

        final String projectKey = getProjectKey(MH_DEV_ADMIN);
        final ApiRoot client = createApiClient(MH_DEV_ADMIN);

        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

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
        Task06c_GRAPHQL_Nodes task06C_graphqlNodes = new Task06c_GRAPHQL_Nodes();

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        String clientId = prop.getProperty(MH_DEV_ADMIN + "clientId");
        String clientSecret = prop.getProperty(MH_DEV_ADMIN + "clientSecret");

        ThirdPartyClientService thirdPartyClientService = new ThirdPartyClientService();
        String token = thirdPartyClientService.createClientAndFetchToken(clientId, clientSecret);
        task06C_graphqlNodes.fetchProductTotalsViaGraphQLandNodes(token, projectKey);
        System.exit(0);
    }
}
