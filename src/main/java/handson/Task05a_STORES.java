package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.ApiPrefixHelper;

import handson.impl.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


/**
 *
 */
public class Task05a_STORES {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger("commercetools");

        final String customerKey = "";
        final String storeKey = "";

        // TODO: Create in-store cart with global API client
        //  1. Provide an API client with global permissions
        //  2. Provide a customer who is restricted to a store
        //  Note: A global cart creation should fail but an in-store cart should world
        //
        final String globalApiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot client = createApiClient(globalApiClientPrefix);

        CustomerService customerService = new CustomerService(client);

        logger.info("Created in-store cart with a global api client: " +
                " "
        );


        // TODO: Create in-store Cart with in-store API client
        //  1. Update the ApiPrefixHelper with the prefix for Store API Client
        //  2. Provide an API client with scope limited to a store
        //  3. Provide a customer with only store permissions
        //  4. Try creating a global cart with a global customer and check the error message

//        final String storeApiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
//        final ProjectApiRoot storeClient = createApiClient(storeApiClientPrefix);

        logger.info("Created in-store cart with a store api client: "+
                " "
        );

        client.close();
//        storeClient.close();

        // TODO
        //  Verify on impex that the carts are holding the same information

    }
}
