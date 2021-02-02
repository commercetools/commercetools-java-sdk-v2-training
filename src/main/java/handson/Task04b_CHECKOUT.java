package handson;

import com.commercetools.api.client.ApiRoot;
import com.commercetools.api.models.channel.Channel;
import com.commercetools.api.models.order.OrderState;
import com.commercetools.api.models.state.State;
import handson.impl.*;
import io.vrap.rmf.base.client.ApiHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getProjectKey;


/**
 * Create a cart for a customer, add a product to it, create an order from the cart and change the order state.
 *
 * See:
 */
public class Task04b_CHECKOUT {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {


        final String apiClientPrefix = PrefixHelper.getDevApiClientPrefix();

        final String projectKey = getProjectKey(apiClientPrefix);
        final ApiRoot client = createApiClient(apiClientPrefix);

        try (ApiHttpClient apiHttpClient = ClientService.apiHttpClient) {

            CustomerService customerService = new CustomerService(client, projectKey);
            CartService cartService = new CartService(client, projectKey);
            OrderService orderService = new OrderService(client, projectKey);
            PaymentService paymentService = new PaymentService(client, projectKey);
            Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());


            // TODO: Fetch a channel if your inventory mode will not be NONE
            //
            Channel channel = null;

            final State state = null;


            // TODO: Perform cart operations:
            //      Get Customer, create cart, add products, add inventory mode
            //      add discount codes, perform a recalculation
            // TODO: Convert cart into an order, set order status, set state in custom work
            //
            // TODO: add payment
            // TAKE CARE: Take off payment for second or third try OR change the interfaceID with a timestamp
            //
            // TODO additionally: add custom line items, add shipping method
            //
            logger.info("Created cart/order ID: " +
                    ""
            );
        }

    }
}
