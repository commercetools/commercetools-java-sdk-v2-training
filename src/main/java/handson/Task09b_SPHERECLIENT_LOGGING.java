package handson;


import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.defaultconfig.ApiFactory;
import com.commercetools.api.defaultconfig.ServiceRegion;
import handson.impl.ClientService;
import io.vrap.rmf.base.client.*;
import io.vrap.rmf.base.client.http.RetryMiddleware;
import io.vrap.rmf.base.client.oauth2.AnonymousSessionTokenSupplier;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import io.vrap.rmf.base.client.oauth2.GlobalCustomerPasswordTokenSupplier;
import io.vrap.rmf.base.client.oauth2.StaticTokenSupplier;
import io.vrap.rmf.okhttp.VrapOkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;

public class Task09b_SPHERECLIENT_LOGGING {


    public static final String MH_DEV_ADMIN = "mh-dev-admin.";

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey(MH_DEV_ADMIN);
        final ApiRoot client = createApiClient(MH_DEV_ADMIN);
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

        // TODO

            // 1
            // Intensive logging, inspect returning headers, log all calls

            // 2
            // ReUse tokens

            // 3
            // Arrays-asList for UpdateActions

            // 4
            // Decorate client for X-Correlation-ID
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String clientId = prop.getProperty(MH_DEV_ADMIN + "clientId");
        String clientSecret = prop.getProperty(MH_DEV_ADMIN + "clientSecret");

        final ApiRoot correlationIdClient = ApiFactory.create(
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl(),
                new ArrayList<>(Arrays.asList(
                        (request, next) -> {
                            request.withHeader(ApiHttpHeaders.X_CORRELATION_ID, projectKey + "/" + UUID.randomUUID().toString());
                            return next.apply(request);
                        },
                        (request, next) -> next.apply(request).whenComplete((response, throwable) -> {
                            if (throwable.getCause() instanceof ApiHttpException) {
                                logger.info(((ApiHttpException)throwable.getCause()).getHeaders().getFirst(ApiHttpHeaders.X_CORRELATION_ID));
                            } else {
                                logger.info(response.getHeaders().getFirst(ApiHttpHeaders.X_CORRELATION_ID));
                            }
                        })
                ))
        );

            // 5
            // Simulate failover, 5xx errors


        final ApiRoot retryClient = ApiFactory.create(
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl(),
                new ArrayList<>(Collections.singletonList(
                        new RetryMiddleware(3, Arrays.asList(500, 503))
                ))
        );
    }

    static class ClientFactory {
        public static ApiRoot createStatic(
                final String token,
                final String apiEndpoint
        ) {
            AuthenticationToken t = new AuthenticationToken();
            t.setAccessToken(token);
            return ApiRoot.fromClient(
                    io.vrap.rmf.base.client.ClientFactory.create(
                            apiEndpoint,
                            new VrapOkHttpClient(),
                            new StaticTokenSupplier(t)
                    )
            );
        }

        public static ApiRoot createAnonFlow(
                final ClientCredentials credentials,
                final String tokenEndpoint,
                final String apiEndpoint
        ) {
            return ApiRoot.fromClient(
                    io.vrap.rmf.base.client.ClientFactory.create(
                            apiEndpoint,
                            new VrapOkHttpClient(),
                            new AnonymousSessionTokenSupplier(
                                    credentials.getClientId(),
                                    credentials.getClientSecret(),
                                    credentials.getScopes(),
                                    tokenEndpoint,
                                    new VrapOkHttpClient()
                            )
                    )
            );
        }

        public static ApiRoot createPasswordFlow(
                final String userEmail,
                final String userPassword,
                final ClientCredentials credentials,
                final String tokenEndpoint,
                final String apiEndpoint
        ) {
            return ApiRoot.fromClient(
                    io.vrap.rmf.base.client.ClientFactory.create(
                            apiEndpoint,
                            new VrapOkHttpClient(),
                            new GlobalCustomerPasswordTokenSupplier(
                                    credentials.getClientId(),
                                    credentials.getClientSecret(),
                                    userEmail,
                                    userPassword,
                                    credentials.getScopes(),
                                    tokenEndpoint,
                                    new VrapOkHttpClient()
                            )
                    )
            );
        }
    }
}
