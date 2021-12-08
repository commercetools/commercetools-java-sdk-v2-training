package handson.impl;

import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.Money;
import com.commercetools.importapi.models.common.ProductKeyReferenceBuilder;
import com.commercetools.importapi.models.common.ProductVariantKeyReferenceBuilder;
import com.commercetools.importapi.models.importcontainers.ImportContainer;
import com.commercetools.importapi.models.importcontainers.ImportContainerDraftBuilder;
import com.commercetools.importapi.models.importrequests.ImportResponse;
import com.commercetools.importapi.models.importrequests.PriceImportRequest;
import com.commercetools.importapi.models.importrequests.PriceImportRequestBuilder;
import com.commercetools.importapi.models.prices.PriceImportBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class ImportService {

    final ProjectApiRoot apiRoot;

    public ImportService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }

    public CompletableFuture<ApiHttpResponse<ImportContainer>> createImportContainer(final String containerKey) {

            return
                apiRoot
                        .importContainers()
                        .post(
                                ImportContainerDraftBuilder.of()
                                       .key(containerKey)
                                       .build()
                        )
                        .execute();
        }


    public CompletableFuture<ApiHttpResponse<ImportResponse>> createPriceImportRequest(
            final String containerKey,
            final String productKey,
            final String productVariantKey,
            final Money amount) {

        final PriceImportRequest resources = PriceImportRequestBuilder.of()
                .resources(
                    PriceImportBuilder.of()
                            .key(productKey + Math.random())        // key for ResourceImport, not the Sink
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
                .build();

            return
                apiRoot
                        .prices()
                        .importContainers()
                        .withImportContainerKeyValue(containerKey)
                        .post(
                                resources
                        )
                        .execute();
    }





}
