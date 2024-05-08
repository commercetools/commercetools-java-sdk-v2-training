package handson;

import com.commercetools.api.client.ProjectApiRoot;
import handson.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task06b_SEARCHQUERY {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        final String storeKey = getStoreKey(apiClientPrefix);
        CustomerService customerService = new CustomerService(client, storeKey);

        OrderService orderService = new OrderService(client);

        logger.info("Today's orders: " +
            client
                .orders()
                .search()
                .post(
                    orderSearchRequestBuilder -> orderSearchRequestBuilder
                        .withQuery(
                            orderSearchQueryBuilder -> orderSearchQueryBuilder
                                .dateRange(orderSearchDateRangeValueBuilder -> orderSearchDateRangeValueBuilder
                                    .field("createdAt")
                                    .gte(LocalDate.now(ZoneId.of("CET")).atStartOfDay(ZoneId.of("CET")))
                                )
                        )
                )
                .executeBlocking()
                .getBody().getTotal()
        );

        client.close();
    }
}
