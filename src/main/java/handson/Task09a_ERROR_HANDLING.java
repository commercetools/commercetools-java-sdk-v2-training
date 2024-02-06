package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer.Customer;
import com.commercetools.api.models.customer.CustomerBuilder;
import handson.impl.ApiPrefixHelper;
import handson.impl.CustomerService;
import io.vrap.rmf.base.client.ApiHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;


public class Task09a_ERROR_HANDLING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        final ProjectApiRoot client = createApiClient(apiClientPrefix);
        Logger logger = LoggerFactory.getLogger("commercetools");

        CustomerService customerService = new CustomerService(client);

        // TODO:
        //  Provide a WRONG or CORRECT customer key
        //
        final String customerKeyMayOrMayNotExist = "customer-michele-WRONG-KEY";

        // TODO: Handle 4XX errors, exceptions
        //  Use CompletionStage
        //
        logger.info("Customer fetch: " +
                " "
        );


        // TODO: Handle 4XX errors, exceptions
        //  Use Optionals, Either (Java 9+)
        //
        Optional<Customer> optionalCustomer = Optional.ofNullable(
                customerService
                        .getCustomerByKey("customer-michele-WRONG-KEY")
                        .thenApply(ApiHttpResponse::getBody)
                        .exceptionally(throwable -> null)
                        .get()
        );

        // Handle now


    }
}
