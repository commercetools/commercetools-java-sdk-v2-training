package handson;


import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.MoneyBuilder;
import handson.impl.ApiPrefixHelper;
import handson.impl.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createImportApiClient;


public class Task03b_IMPORT_API {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // TODO
        //  Update your prefix for an Import Api Client in the PrefixHelper
        //  Provide a price sink key
        //
        final String apiImportClientPrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();
        final String sinkKey = "berlin-store-prices";

        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());
        final ProjectApiRoot client = createImportApiClient(apiImportClientPrefix);
        final ImportService importService = new ImportService(client);


        // TODO
        //  CREATE a price import sink
        //  CREATE a price import request
        //  CHECK the status of your import requests
        //
        logger.info("Created import price sink {} ",
                importService.createImportPriceSink(sinkKey)
                        .toCompletableFuture().get()
        );

        Money amount = MoneyBuilder.of()
                .currencyCode("EUR")
                .centAmount(3412L)
                .build();

        logger.info("Created price resource {} ",
                importService.createPriceImportRequest(sinkKey,"tulip-seed-product","TULIPSEED01", amount)
                        .toCompletableFuture().get()
        );

        logger.info("Report on all queued import operations on our price import sink {} ",
                client
                        .importContainers()
                        .withImportContainerKeyValue(sinkKey)
                        .importSummaries()
                        .get()
                        .execute()
                        .toCompletableFuture().get()
                        .getBody().getStates().getImported()
        );

        client.close();
    }
}

