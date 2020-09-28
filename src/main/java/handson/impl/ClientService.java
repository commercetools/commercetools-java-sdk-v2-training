package handson.impl;

// import com.commercetools.importer.client.ApiRoot;
import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.defaultconfig.ApiFactory;
import com.commercetools.importapi.defaultconfig.ImportApiFactory;
import com.commercetools.ml.defaultconfig.MLApiRootFactory;

import java.io.IOException;
import java.util.Properties;

public class ClientService {


    // TODO: Add the Constant-Token Client

    /**
     * @throws IOException
     */
    public static ApiRoot createApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return
                ApiFactory.create(
                    prop.getProperty(prefix + "clientId"),
                    prop.getProperty(prefix + "clientSecret"),
                    prop.getProperty(prefix + "scopes"),
                    prop.getProperty(prefix + "authUrl"),
                    prop.getProperty(prefix + "apiUrl")
                );
    }

    /**
     * @throws IOException
     * @return
     */
    public static com.commercetools.importapi.client.ApiRoot createImportApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return
                ImportApiFactory.create(
                        prop.getProperty(prefix + "clientId"),
                        prop.getProperty(prefix + "clientSecret"),
                        prop.getProperty(prefix + "scopes"),
                        prop.getProperty(prefix + "authUrl"),
                        prop.getProperty(prefix + "apiUrl")
                );
    }

    /**
     * @throws IOException
     * @return
     */
    public static com.commercetools.ml.client.ApiRoot createMLApiClient(final String prefix) throws IOException {

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return
                MLApiRootFactory.create(
                        prop.getProperty(prefix + "clientId"),
                        prop.getProperty(prefix + "clientSecret"),
                        prop.getProperty(prefix + "scopes"),
                        prop.getProperty(prefix + "authUrl"),
                        prop.getProperty(prefix + "apiUrl")
                );
    }

    public static Object createConstantTokenApiClient(final String prefix, String token) throws IOException {

        // checkout out comments from Jens


        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return
                null;
    }


    public static Object createMeTokenApiClient(final String prefix, String token) throws IOException {

        // checkout out comments from Jens

        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));

        return
                null;
    }



}