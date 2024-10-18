package handson;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.order.*;
import handson.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createApiClient;
import static handson.impl.ClientService.getStoreKey;


public class Task06b_SEARCHQUERY {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String apiClientPrefix = ApiPrefixHelper.API_STORE_CLIENT_PREFIX.getPrefix();
        try (ProjectApiRoot apiRoot = createApiClient(apiClientPrefix)) {
            Logger logger = LoggerFactory.getLogger("commercetools");

            final String storeKey = getStoreKey(apiClientPrefix);

            logger.info("Today's orders: " +
                    apiRoot
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

            logger.info("Orders with a particular SKU: " +
                    apiRoot
                            .orders()
                            .search()
                            .post(
                                    r -> r.withQuery(q -> q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3P")))
                            )
                            .executeBlocking()
                            .getBody().getTotal()
            );

            logger.info("orders: " +
                    apiRoot
                            .orders()
                            .search()
                            .post(
                                    r -> r.withQuery(
                                            q -> q.and(
                                                    a -> a.addAnd(aa -> aa.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3P")))
                                                        .addAnd(aa -> aa.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3H")))
                                            )
                                    )


                            )
                            .executeBlocking()
                            .getBody().getTotal()
            );

            logger.info("Orders with SKUs: " +
                    apiRoot
                            .orders()
                            .search()
                            .post(
                                    r -> r.withQuery(
                                            q -> q.and(
                                                    q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3P")),
                                                    q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000FHAK")),
                                                    q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000FHAL"))
                                                )
                                    )
                            )
                            .executeBlocking()
                            .getBody().getTotal()
            );
        }
    }
}
