package handson;


import com.commercetools.importapi.client.ApiRoot;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.MoneyBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import handson.impl.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createImportApiClient;
import static handson.impl.ClientService.getProjectKey;


public class Task03b_IMPORT_API {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        // TODO
        //  Check your prefix for an Import Api Client
        //  Provide a price sink key
        //
        String apiImportClientPrefix = "mh-dev-import.";
        final String sinkKey = "berlin-store-prices";
        final String projectKey = getProjectKey("mh-import-admin.");

        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());
        final ApiRoot client = createImportApiClient(apiImportClientPrefix);
        final ImportService importService = new ImportService(client, projectKey);


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
                                .getBody().getResults().get(0).getResourceType()
                        );
            logger.info("Report on all queued import operations on our price import sink {} " +
                            client
                                .withProjectKeyValue(projectKey)
                                .importSummaries()
                                .importSinkKeyWithImportSinkKeyValue(sinkKey)
                                .get()
                                .execute()
                                .toCompletableFuture().get()
                                .getBody().getStates().getImported()
                    );

    }
}

