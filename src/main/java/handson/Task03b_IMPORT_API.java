package handson;


import com.commercetools.importapi.client.ApiRoot;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.MoneyBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import handson.impl.ImportService;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static handson.impl.ClientService.createImportApiClient;


public class Task03b_IMPORT_API {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String sinkKey = "berlin-store-prices";
        final String projectKey = "raining-011-avensia-test";

        Logger logger = Logger.getLogger(Task02b_UPDATE_Group.class.getName());
        // TODO
        //  Get an API Client for Import-API
        //
        final ApiRoot client = createImportApiClient("mh-import-admin.");
        final ImportService importService = new ImportService(client, projectKey);
        ObjectMapper objectMapper = new ObjectMapper();

        // TODO
        //  CREATE a price import sink
        //  CREATE a price import request
        //  CHECK the status of your import requests
        //

            logger.info("Created import price sink {} " +
                    importService.createImportPriceSink(sinkKey)
                    .toCompletableFuture().get()
            );

            Money amount = MoneyBuilder.of()
                    .currencyCode("EUR")
                    .centAmount(12365L)
                    .build();

            logger.info("Created price resource {} " +
                    importService.createPriceImportRequest(sinkKey,"testforpdf","0812", amount)
                    .toCompletableFuture().get()
            );

            logger.info("Summary report on all sinks on our project: " +
                            client
                                .withProjectKeyValue(projectKey)
                                .importSinks()
                                .get()
                                .execute()
                                .toCompletableFuture().get()
                        );
            logger.info("Report on all queued import operations on our price import sink {} " +
                            client
                                .withProjectKeyValue(projectKey)
                                .importSummaries()
                                .importSinkKeyWithImportSinkKeyValue(sinkKey)
                                .get()
                                .execute()
                                .toCompletableFuture().get()
                    );

    }
}

