package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiFactory;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.importapi.defaultconfig.ImportApiRootBuilder;
import io.vrap.rmf.base.client.ApiHttpClient;
import io.vrap.rmf.base.client.AuthenticationToken;
import io.vrap.rmf.base.client.HttpClientSupplier;
import io.vrap.rmf.base.client.oauth2.*;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ClientService {

    public static ProjectApiRoot projectApiRoot;
    public static com.commercetools.importapi.client.ProjectApiRoot importHttpClient;

    // TODO: Add the Constant-Token Client

    /**
     * @throws IOException exception
     */
    public static ProjectApiRoot createApiClient(final String prefix) throws IOException {

        projectApiRoot = null;
        return projectApiRoot;
    }

    public static String getProjectKey(final String prefix) throws IOException {
        return null;
    }

    public static String getClientId(final String prefix) throws IOException {
        return null;
    }

    public static String getClientSecret(final String prefix) throws IOException {
        return null;
    }

    public static String getStoreKey(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return prop.getProperty(prefix + "storeKey");
    }

    public static String getCustomerEmail(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return prop.getProperty(prefix + "customerEmail");
    }

    public static String getCustomerPassword(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return prop.getProperty(prefix + "customerPassword");
    }

    /**
     * @return apiRoot
     * @throws IOException exception
     */
    public static com.commercetools.importapi.client.ProjectApiRoot createImportApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");
        String projectKey = prop.getProperty(prefix + "projectKey");

        importHttpClient = ImportApiRootBuilder.of().defaultClient(
                ClientCredentials.of()
                        .withClientId(clientId)
                        .withClientSecret(clientSecret)
                    .build(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
        )
        .buildProjectRoot(projectKey);

        return importHttpClient;
    }


    public static ProjectApiRoot createMeTokenApiClient(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String projectKey = prop.getProperty(prefix + "projectKey");
        String customerEmail = prop.getProperty(prefix + "customerEmail");
        String customerPassword = prop.getProperty(prefix + "customerPassword");
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        return ApiRootBuilder.of().defaultClient(
                     ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
                )
                .withGlobalCustomerPasswordFlow(
                        ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                            .build(),
                    customerEmail,
                    customerPassword,
                    ServiceRegion.GCP_EUROPE_WEST1.getAuthUrl() + "/oauth/" + projectKey + "/customers/token"
                )
                .buildProjectRoot(projectKey);
    }

    public static ProjectApiRoot createStoreMeApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String projectKey = prop.getProperty(prefix + "projectKey");
        String storeKey = prop.getProperty(prefix + "storeKey");
        String storeCustomerEmail = prop.getProperty(prefix + "customerEmail");
        String storeCustomerPassword = prop.getProperty(prefix + "customerPassword");
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        return ApiRootBuilder.of().defaultClient(ServiceRegion.GCP_EUROPE_WEST1.getApiUrl())
                .withGlobalCustomerPasswordFlow(
                        ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                            .build(),
                        storeCustomerEmail,
                        storeCustomerPassword,
                        ServiceRegion.GCP_EUROPE_WEST1.getAuthUrl() + "/oauth/" + projectKey + "/in-store/key=" + storeKey + "/customers/token"
                )
                .buildProjectRoot(projectKey);
    }

    public static ApiRoot createConstantTokenApiClient(String token) throws IOException {

        final ApiHttpClient apiHttpClient = ClientFactory.createStatic(
                token,
                "https://api.europe-west1.gcp.commercetools.com/"
            );

        return ApiFactory.create(() -> apiHttpClient);
    }

    public static AuthenticationToken getTokenForClientCredentialsFlow(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String projectKey = prop.getProperty(prefix + "projectKey");
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");
        AuthenticationToken token = null;
        try (final ClientCredentialsTokenSupplier clientCredentialsTokenSupplier = new ClientCredentialsTokenSupplier(
                clientId,
                clientSecret,
                null,
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                HttpClientSupplier.of().get()
        )) {
            token = clientCredentialsTokenSupplier.getToken().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return token;
    }



    static class ClientFactory {
        public static ApiHttpClient createStatic(
                final String token,
                final String apiEndpoint
        ) {
            AuthenticationToken t = new AuthenticationToken();
            t.setAccessToken(token);
            return io.vrap.rmf.base.client.ClientFactory.create(
                    apiEndpoint,
                    HttpClientSupplier.of().get(),
                    new StaticTokenSupplier(t)
            );
        }

        public static ApiHttpClient createAnonFlow(
                final ClientCredentials credentials,
                final String tokenEndpoint,
                final String apiEndpoint
        ) {
            return io.vrap.rmf.base.client.ClientFactory.create(
                    apiEndpoint,
                    HttpClientSupplier.of().get(),
                    new AnonymousSessionTokenSupplier(
                            credentials.getClientId(),
                            credentials.getClientSecret(),
                            credentials.getScopes(),
                            tokenEndpoint,
                            HttpClientSupplier.of().get()
                    )
            );
        }

        public static ApiHttpClient createPasswordFlow(
                final String userEmail,
                final String userPassword,
                final ClientCredentials credentials,
                final String tokenEndpoint,
                final String apiEndpoint
        ) {
            return io.vrap.rmf.base.client.ClientFactory.create(
                    apiEndpoint,
                    HttpClientSupplier.of().get(),
                    new GlobalCustomerPasswordTokenSupplier(
                            credentials.getClientId(),
                            credentials.getClientSecret(),
                            userEmail,
                            userPassword,
                            credentials.getScopes(),
                            tokenEndpoint,
                            HttpClientSupplier.of().get()
                    )
            );
        }
    }
}
