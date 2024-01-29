package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import handson.impl.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


/**
 * Configure sphere client and get project information.
 *
 * See:
 *  TODO dev.properties
 *  TODO {@link ClientService#createApiClient(String prefix)}
 */
public class Task02a_CREATE {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        /**
         * TODO:
         * UPDATE the ApiPrefixHelper with your prefix from dev.properties (e.g. "mh-dev-admin.")
         */
        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        CustomerService customerService = new CustomerService(client);

            logger.info("Customer fetch: " +
                    ""
            );

            // TODO:
            //  CREATE a customer
            //  CREATE a email verification token
            //  Verify customer
            //
            logger.info("Customer created: " +
                    ""
            );


        client.close();
    }
}
