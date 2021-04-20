package handson;


import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.api.models.customer.CustomerSetFirstNameActionBuilder;
import com.commercetools.api.models.customer.CustomerSetLastNameActionBuilder;
import com.commercetools.api.models.customer.CustomerUpdateBuilder;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.ApiHttpClient;
import io.vrap.rmf.base.client.ApiHttpException;
import io.vrap.rmf.base.client.ApiHttpHeaders;
import io.vrap.rmf.base.client.HttpClientSupplier;
import io.vrap.rmf.base.client.http.RetryMiddleware;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.commercetools.api.defaultconfig.ApiFactory.create;
import static com.commercetools.api.defaultconfig.ApiFactory.defaultClient;
import static handson.impl.ClientService.*;

public class Task09b_SPHERECLIENT_LOGGING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {


        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);
        final String clientId = getClientId(apiClientPrefix);
        final String clientSecret = getClientSecret(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger(Task09b_SPHERECLIENT_LOGGING.class.getName());

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
            client
                .withProjectKey(projectKey)
                .customers()
                .withKey("myCustomerKey")
                .post(CustomerUpdateBuilder.of()
                        .version(1l)
                        .actions(
                                        CustomerSetFirstNameActionBuilder.of()
                                            .firstName("his new first name ")
                                            .build()
                        )
                        .build())
                .execute();
            client
                .withProjectKey(projectKey)
                .customers()
                .withKey("myCustomerKey")
                .post(CustomerUpdateBuilder.of()
                        .version(1l)
                        .actions(
                                        CustomerSetLastNameActionBuilder.of()
                                                .lastName("his new last name")
                                                .build()
                        )
                        .build())
                .execute();


            client
                .withProjectKey(projectKey)
                .customers()
                .withKey("myCustomerKey")
                .post(CustomerUpdateBuilder.of()
                        .version(1l)
                        .actions(
                                Arrays.asList(
                                        CustomerSetFirstNameActionBuilder.of()
                                                .firstName("his new first name ")
                                                .build(),
                                        CustomerSetLastNameActionBuilder.of()
                                                .lastName("his new last name")
                                                .build()
                                )
                        )
                        .build())
                .execute();


            // 4: X-Correlation_ID
            //      Decorate client
            //      Be careful!!
            //      Run GET Project and inspect x-correlation-id in the headers

            try (ApiHttpClient correlationIdApiHttpClient = defaultClient(
                HttpClientSupplier.of().get(),
                ClientCredentials.of()
                        .withClientId(clientId)
                        .withClientSecret(clientSecret)
                        .build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl(),
                Collections.singletonList(
                        (request, next) -> next.apply(request).whenComplete((response, throwable) -> {
                            if (throwable.getCause() instanceof ApiHttpException) {
                                logger.info(((ApiHttpException)throwable.getCause()).getHeaders().getFirst(ApiHttpHeaders.X_CORRELATION_ID));
                            } else {
                                logger.info(response.getHeaders().getFirst(ApiHttpHeaders.X_CORRELATION_ID));
                            }
                        })
                ),
                () -> projectKey + "/" + UUID.randomUUID().toString()
            )) {
            final ApiRoot correlationIdClient = create(() -> correlationIdApiHttpClient);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Or, per request

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {
            logger.info("Get project information with pre-set correlation id: " +
                    client
                        .withProjectKey(projectKey)
                        .get()
                        .withHeader(ApiHttpHeaders.X_CORRELATION_ID, "MyServer15" + UUID.randomUUID().toString())
                        .execute()
                        .toCompletableFuture().get()
                        .getBody().getKey()
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // 5
        //      Simulate failover, 5xx errors
        //      Nice test: Replace with
        //                  new RetryMiddleware(20, Arrays.asList(404, 500, 503))
        //                  and query for wrong customer, inspect then logging about the re-tries

        try (ApiHttpClient retryHttpClient = defaultClient(
                HttpClientSupplier.of().get(),
                ClientCredentials.of()
                        .withClientId(clientId)
                        .withClientSecret(clientSecret)
                        .build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl(),
                new ArrayList<>(Collections.singletonList(
                        new RetryMiddleware(3, Arrays.asList(500, 503))
                ))
        )) {
            ApiRoot retryClient = create(() -> retryHttpClient);
            logger.info("Get project information via retryClient " +
                    retryClient
                            .withProjectKey(projectKey)
                            .get()
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getKey()
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
