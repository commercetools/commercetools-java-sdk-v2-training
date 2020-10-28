package handson.impl;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.defaultconfig.ApiFactory;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.importapi.defaultconfig.ImportApiFactory;
import com.commercetools.ml.defaultconfig.MLApiRootFactory;
import io.vrap.rmf.base.client.ApiHttpClient;

import io.vrap.rmf.base.client.AuthenticationToken;
import io.vrap.rmf.base.client.VrapHttpClient;
import io.vrap.rmf.base.client.oauth2.AnonymousSessionTokenSupplier;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import io.vrap.rmf.base.client.oauth2.GlobalCustomerPasswordTokenSupplier;
import io.vrap.rmf.base.client.oauth2.StaticTokenSupplier;
import io.vrap.rmf.okhttp.VrapOkHttpClient;

import java.io.IOException;
import java.util.Properties;

public class ClientService {

    public static ApiHttpClient apiHttpClient;
    public static ApiHttpClient importHttpClient;
    public static ApiHttpClient mlHttpClient;

    // TODO: Add the Constant-Token Client

    /**
     * @throws IOException
     */
    public static ApiRoot createApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        apiHttpClient = ApiFactory.defaultClient(
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
        );
        return ApiFactory.create(() -> apiHttpClient);
    }

    public static String getProjectKey(final String prefix) throws IOException {
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return prop.getProperty(prefix + "projectKey");
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
     * @return
     * @throws IOException
     */
    public static com.commercetools.importapi.client.ApiRoot createImportApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        importHttpClient = ImportApiFactory.defaultClient(
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
        );

        return ImportApiFactory.create(() -> importHttpClient);
    }

    public static ApiRoot createStoreApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String projectKey = prop.getProperty(prefix + "projectKey");
        String storeKey = prop.getProperty(prefix + "storeKey");
        String storeCustomerEmail = prop.getProperty(prefix + "customerEmail");
        String storeCustomerPassword = prop.getProperty(prefix + "customerPassword");
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        final ApiHttpClient apiHttpClient = ClientFactory.createPasswordFlow(
                    storeCustomerEmail,
                    storeCustomerPassword,
                    ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                    "https://auth.europe-west1.gcp.commercetools.com/oauth/" + projectKey + "/in-store/key=" + storeKey + "/customers/token",
                    "https://api.europe-west1.gcp.commercetools.com/"
                );
        return ApiFactory.create(() -> apiHttpClient);
    }

    public static ApiRoot createMeTokenApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String projectKey = prop.getProperty(prefix + "projectKey");
        String customerEmail = prop.getProperty(prefix + "customerEmail");
        String customerPassword = prop.getProperty(prefix + "customerPassword");
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        final ApiHttpClient apiHttpClient = ClientFactory.createPasswordFlow(
                customerEmail,
                customerPassword,
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                "https://auth.europe-west1.gcp.commercetools.com/oauth/" + projectKey + "/customers/token",
                "https://api.europe-west1.gcp.commercetools.com/"
        );

        return ApiFactory.create(() -> apiHttpClient);
    }





    public static ApiRoot createConstantTokenApiClient(String token) throws IOException {

        final ApiHttpClient apiHttpClient = ClientFactory.createStatic(
                token,
                "https://api.europe-west1.gcp.commercetools.com/"
            );

        return ApiFactory.create(() -> apiHttpClient);
    }



    /**
     * @return
     * @throws IOException
     */
    public static com.commercetools.ml.client.ApiRoot createMLApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        String clientId = prop.getProperty(prefix + "clientId");
        String clientSecret = prop.getProperty(prefix + "clientSecret");

        mlHttpClient = MLApiRootFactory.defaultClient(
                ClientCredentials.of().withClientId(clientId).withClientSecret(clientSecret).build(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getOAuthTokenUrl(),
                com.commercetools.importapi.defaultconfig.ServiceRegion.GCP_EUROPE_WEST1.getApiUrl()
        );

        return MLApiRootFactory.create(() -> mlHttpClient);
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
                            new VrapOkHttpClient(),
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
                            new VrapOkHttpClient(),
                            new AnonymousSessionTokenSupplier(
                                    credentials.getClientId(),
                                    credentials.getClientSecret(),
                                    credentials.getScopes(),
                                    tokenEndpoint,
                                    new VrapOkHttpClient()
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
            );
        }
    }
}
