package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.graph_ql.GraphQLRequest;
import com.commercetools.graphql.api.GraphQL;
import com.commercetools.graphql.api.GraphQLResponse;
import com.commercetools.graphql.api.types.ProductQueryResult;
import handson.impl.ApiPrefixHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.*;


public class Task06c_GRAPHQL {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());


        // TODO:
        //  Use the GraphQL playground to create a graphql query
        //

        logger.info("GraphQl : " +
                client
                        .graphql()
                        .post(
                                graphQLRequestBuilder -> graphQLRequestBuilder
                                      .query("{ products { total }}")
                        )
                        .execute()
                        .get()
                        .getBody()
                        .getData()
        );

        GraphQLResponse<ProductQueryResult> responseEntity =
                client
                        .graphql()
                        .query(GraphQL.products(q -> q.limit(3).sort(Collections.singletonList("masterData.current.name.en desc")))
                                      .projection(p -> p.total().results().id().masterData().current().name("en", null)))
                        .executeBlocking()
                        .getBody();
        logger.info("Total products: " + responseEntity.getData().getTotal());
        responseEntity.getData().getResults().forEach(result ->
                    logger.info("Id: " + result.getId() + "Name: " + result.getMasterData().getCurrent().getName()));

        client.close();
    }
}
