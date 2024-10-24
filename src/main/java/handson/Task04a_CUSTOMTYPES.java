package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerSetCustomFieldActionBuilder;
import com.commercetools.api.models.customer.CustomerSetCustomTypeActionBuilder;
import com.commercetools.api.models.order.Order;
import com.commercetools.api.models.order.OrderSetCustomFieldActionBuilder;
import com.commercetools.api.models.order.OrderSetCustomTypeActionBuilder;
import com.commercetools.api.models.type.*;
import handson.impl.ApiPrefixHelper;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task04a_CUSTOMTYPES {


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {

            Logger logger = LoggerFactory.getLogger("commercetools");
            final String storeKey = getStoreKey(apiClientPrefix);

            // TODO CREATE labels for the type and its fields
            //
            Map<String, String> nameForType = new HashMap<String, String>() {
                {
                    put("de-DE", "Delivery instructions");
                    put("en-US", "Delivery instructions");
                }
            };

            // TODO DEFINE fields
            //
            // List<FieldDefinition> definitions = Arrays.asList()

            // TODO CREATE type
            //
            // client.types.post().execute()


            Order order = apiRoot       // GET order by order number
                    .inStore(storeKey)
                    .orders()
                    .withOrderNumber("")
                    .get()
                    .executeBlocking().getBody();
            //TODO UPDATE the Order with custom type
            //
            //  apiRoot.inStore(storeKey).orders().withId(order.getId()).post().execute()


            Customer customer = apiRoot   // GET customer by key
                    .inStore(storeKey)
                    .customers()
                    .withKey("")
                    .get()
                    .executeBlocking().getBody();

            // TODO UPDATE the customer with custom type
            //
            // client.inStore(storeKey).customers().withKey(customerKey).post().execute()
        }
    }
}
