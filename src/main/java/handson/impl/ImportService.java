package handson.impl;

import com.commercetools.importapi.client.ApiRoot;
import com.commercetools.importapi.models.common.ImportResourceType;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.ProductKeyReferenceBuilder;
import com.commercetools.importapi.models.common.ProductVariantKeyReferenceBuilder;
import com.commercetools.importapi.models.importrequests.ImportResponse;
import com.commercetools.importapi.models.importrequests.PriceImportRequest;
import com.commercetools.importapi.models.importrequests.PriceImportRequestBuilder;
import com.commercetools.importapi.models.importsinks.ImportSink;
import com.commercetools.importapi.models.importsinks.ImportSinkDraftBuilder;
import com.commercetools.importapi.models.prices.PriceImportBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vrap.rmf.base.client.ApiHttpResponse;


import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class ImportService {

    ApiRoot apiRoot;
    String projectKey;

    public ImportService(final ApiRoot client, String projectKey) {
        this.apiRoot = client;
        this.projectKey = projectKey;
    }

    public CompletableFuture<ApiHttpResponse<ImportSink>> createImportPriceSink(final String sinkKey) throws JsonProcessingException {

            return
                apiRoot
                        .withProjectKeyValue(projectKey)
                        .importSinks()
                        .post(
                                ImportSinkDraftBuilder.of()
                                        .key(sinkKey)
                                        .resourceType(ImportResourceType.PRICE)
                                        .build()
                        )
                        .execute();
        }


    public CompletableFuture<ApiHttpResponse<ImportResponse>> createPriceImportRequest(
            final String sinkKey,
            final String productKey,
            final String productVariantKey,
            final Money amount) throws JsonProcessingException {

        final PriceImportRequest resources = PriceImportRequestBuilder.of()
                .resources(
                        Arrays.asList(
                                PriceImportBuilder.of()
                                        .key(sinkKey + "837367")                    // key for ResourceImport, not the Sink
                                        .country("DE")                              // TODO: adjust
                                        .product(ProductKeyReferenceBuilder.of()
                                                .key(productKey)
                                                .build()
                                        )
                                        .productVariant(ProductVariantKeyReferenceBuilder.of()
                                                .key(productVariantKey)             // TODO: check the key!!!
                                                .build()
                                        )
                                        .value(amount)
                                        .build()
                        )
                )
                .build();

            return
                apiRoot
                        .withProjectKeyValue(projectKey)
                        .prices()
                        .importSinkKeyWithImportSinkKeyValue(sinkKey)
                        .post(
                                resources
                        )
                        .execute();
    }





}
