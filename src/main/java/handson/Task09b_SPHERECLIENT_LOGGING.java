package handson;


import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.api.models.customer.CustomerSetFirstNameActionBuilder;
import com.commercetools.api.models.customer.CustomerSetLastNameActionBuilder;
import com.commercetools.api.models.customer.CustomerUpdateBuilder;
import handson.impl.ApiPrefixHelper;
import io.vrap.rmf.base.client.ApiHttpException;
import io.vrap.rmf.base.client.ApiHttpHeaders;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.*;

public class Task09b_SPHERECLIENT_LOGGING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final String projectKey = getProjectKey(apiClientPrefix);
        final ProjectApiRoot apiRoot = createApiClient(apiClientPrefix);
        final String clientId = getClientId(apiClientPrefix);
        final String clientSecret = getClientSecret(apiClientPrefix);


        // TODO 1..5
        //  Execute, inspect individually
        //

        // 1: Logging
        //      Modify logback.xml
        //      Inspect log output, header information, etc.


        // 2: ReUse tokens
        //      See Task06c_GRAPHQL_Nodes.java for an example token fetch
        //      Use ClientService.createConstantTokenApiClient and fetch customers


        // 3: List of UpdateActions
        //      Compare the following three code snippets for updating a customer
        apiRoot
            .customers()
            .withKey("myCustomerKey")
            .post(CustomerUpdateBuilder.of()
                .version(1L)
                .actions(
                    CustomerSetFirstNameActionBuilder.of()
                        .firstName("his new first name ")
                        .build()
                )
                .build())
            .execute();

        apiRoot
            .customers()
            .withKey("myCustomerKey")
            .post(CustomerUpdateBuilder.of()
                .version(1L)
                .actions(
                    CustomerSetLastNameActionBuilder.of()
                            .lastName("his new last name")
                            .build()
                )
                .build())
            .execute();


        apiRoot
            .customers()
            .withKey("myCustomerKey")
            .post(CustomerUpdateBuilder.of()
                .version(1L)
                .actions(
                    CustomerSetFirstNameActionBuilder.of()
                            .firstName("his new first name ")
                            .build(),
                    CustomerSetLastNameActionBuilder.of()
                            .lastName("his new last name")
                            .build()
                )
                .build())
            .execute();


            // 4: X-Correlation_ID
            //      Decorate client
            //      Be careful!!
            //      Run GET Project and inspect x-correlation-id in the headers

        ProjectApiRoot correlationIdApiRoot = ApiRootBuilder.of()
            .defaultClient(
                ClientCredentials.of()
                    .withClientId(clientId)
                    .withClientSecret(clientSecret)
                    .build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
            )
            .withMiddleware((request, next) -> next.apply(request).whenComplete((response, throwable) -> {
                if (throwable.getCause() instanceof ApiHttpException) {
                    logger.info(((ApiHttpException) throwable.getCause()).getHeaders().getFirst(ApiHttpHeaders.X_CORRELATION_ID));
                } else {
                    logger.info(response.getHeaders().getFirst(ApiHttpHeaders.X_CORRELATION_ID));
                }
            }))
            .addCorrelationIdProvider(() -> projectKey + "/" + UUID.randomUUID())
            .build(projectKey);



        // Or, per request

        logger.info("Get project information with pre-set correlation id: " +
            correlationIdApiRoot
                .get()
                .withHeader(ApiHttpHeaders.X_CORRELATION_ID, "MyServer15" + UUID.randomUUID())
                .execute()
                .get()
                .getBody().getKey()
        );

        // 5
        //      Simulate failover, 5xx errors
        //      Nice test: Replace with
//                  new RetryMiddleware(20, Arrays.asList(404, 500, 503))
//                  and query for wrong customer, inspect then logging about the re-tries

        ProjectApiRoot retryApiRoot = ApiRootBuilder.of()
            .defaultClient(
               ClientCredentials.of()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .build(),
               ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
               ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
            )
            .withPolicies(policies -> policies.withRetry(builder -> builder.maxRetries(5)
                .statusCodes(Arrays.asList(502, 503, 504, 404, 400))))
            .build(projectKey);
        logger.info("Get project information via retryClient " +
            retryApiRoot
                .get()
                .execute()
                .get()
                .getBody().getKey()
        );

        ProjectApiRoot concurrentApiRoot = ApiRootBuilder.of()
            .defaultClient(
                ClientCredentials.of()
                    .withClientId(clientId)
                    .withClientSecret(clientSecret)
                    .build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
            )
            .addConcurrentModificationMiddleware(3)
            .build(projectKey);
        logger.info("Update customer via concurrentClient " +
                concurrentApiRoot
                .customers()
                .withKey("customer-michael15")
                .post(CustomerUpdateBuilder.of()
                    .version(1L)
                    .actions(CustomerSetLastNameActionBuilder.of()
                        .lastName("tester1")
                        .build())
                    .build())
                .execute()
                .get()
                .getBody().getLastName()
        );
        concurrentApiRoot.close();
    }
}
