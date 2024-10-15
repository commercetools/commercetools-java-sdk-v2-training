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

        final String apiClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();

        Logger logger = LoggerFactory.getLogger("commercetools");

        final ProjectApiRoot client = createApiClient(apiClientPrefix);

        final String storeKey = getStoreKey(apiClientPrefix);

        logger.info("Today's orders: " +
            client
                .orders()
                .search()
                    .post(
                            r -> r.withQuery(
                                    q -> q.and(
                                            a -> a.addAnd(pa -> pa.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3P")))
                                                    .addAnd(pa -> pa.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3H")))
                                    )
                            )


                    )
                .executeBlocking()
                .getBody().getTotal()
        );

        logger.info("Today's orders: " +
                        client
                                .orders()
                                .search()
                                .post(
                                        r -> r.withQuery(
                                                q -> q.and(
                                                    q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3P")),
                                                    q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3P")),
                                                    q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3P")),
                                                    q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3P")),
                                                    q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3H"))
                                                )
                                        )


                                )
                                .executeBlocking()
                                .getBody().getTotal()
        );

        logger.info("Orders with a particular SKU: " +
                client
                        .orders()
                        .search()
                        .post(
                                r -> r.withQuery(q -> q.exact(e -> e.field("lineItems.variant.sku").value("M0E20000000DG3P")))
                        )
                        .executeBlocking()
                        .getBody().getTotal()
        );

        client.close();
    }
}
