package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.Address;
import com.commercetools.api.models.common.AddressBuilder;
import com.commercetools.api.models.common.AddressDraft;
import com.commercetools.api.models.common.AddressDraftBuilder;
import com.commercetools.api.models.customer.AnonymousCartSignInMode;
import com.commercetools.api.models.order.OrderState;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task05b_ORDER {

    private static final Logger log = LoggerFactory.getLogger(Task05b_ORDER.class);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot client = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final String storeKey = getStoreKey(apiClientPrefix);

            CartService cartService = new CartService(client, storeKey);
            CustomerService customerService = new CustomerService(client, storeKey);
            OrderService orderService = new OrderService(client, storeKey);

            // TODO: UPDATE the cart ID from the previous task
            //
            final String cartId = "";
            final String initialStateKey = "OrderPacked";
            final String orderNumber = "";

            // TODO: Place the order for the cartId above
            // TODO: Set order status to CONFIRMED, set custom workflow state to initial state

        }
    }
}
