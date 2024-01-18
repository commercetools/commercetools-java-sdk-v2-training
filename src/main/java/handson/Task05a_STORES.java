package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.cart.CartDraftBuilder;
import com.commercetools.api.models.me.MyCartDraftBuilder;
import handson.impl.ApiPrefixHelper;

import handson.impl.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.*;


public class Task05a_STORES {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(Task05a_STORES.class.getName());

        final String customerKey = "customer-michael15";
        final String storeKey = "berlin-store";

        // TODO: Create in-store cart with global API client
        //  Provide an API client with global permissions
        //  Provide a customer who is restricted to a store
        //  Note: A global cart creation should fail but an in-store cart should world

        final String globalApiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot client = createApiClient(globalApiClientPrefix);

        CustomerService customerService = new CustomerService(client);

        logger.info("Created in-store cart with a global api client: " +
                customerService.getCustomerByKey(customerKey)
                        .thenComposeAsync(customerApiHttpResponse ->
                                client
                                    .inStore(storeKey)
                                    .carts()
                                    .post(
                                            CartDraftBuilder.of()
                                                    .deleteDaysAfterLastModification(90L)
                                                    .customerId(customerApiHttpResponse.getBody().getId())
                                                    .currency("EUR")
                                                    .customerEmail(customerApiHttpResponse.getBody().getEmail())
                                                    .build()
                                    )
                                    .execute()
                                ).get().getBody().getId()
        );

        // TODO: Create in-store Cart with in-store API client
        //  Update the ApiPrefixHelper with the prefix for Store API Client
        //  Provide an API client with scope limited to a store
        //  Provide a customer with only store permissions
        //  Try creating a global cart with a global customer and check the error message


        final String storeApiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        final ProjectApiRoot storeClient = createApiClient(storeApiClientPrefix);


        logger.info("Created in-store cart with a store api client: "+
                customerService.getCustomerByKey(customerKey)
                        .thenComposeAsync(customerApiHttpResponse ->
                                storeClient
                                    .inStore(storeKey)
                                    .carts()
                                    .post(
                                            CartDraftBuilder.of()
                                                    .deleteDaysAfterLastModification(90L)
                                                    .customerId(customerApiHttpResponse.getBody().getId())
                                                    .currency("EUR")
                                                    .customerEmail(customerApiHttpResponse.getBody().getEmail())
                                                    .build()
                                    )
                                    .execute()
                        )
                        .get().getBody().getId()
        );

        client.close();
        storeClient.close();

        // TODO
        //  Visit impex to verify that the carts are holding the same information

    }
}
