package handson.impl;

import com.commercetools.importapi.client.ProjectApiRoot;
import com.commercetools.importapi.models.common.StoreKeyReferenceBuilder;
import com.commercetools.importapi.models.customers.CustomerAddressBuilder;
import com.commercetools.importapi.models.customers.CustomerImport;
import com.commercetools.importapi.models.customers.CustomerImportBuilder;
import com.commercetools.importapi.models.importcontainers.ImportContainer;
import com.commercetools.importapi.models.importrequests.ImportResponse;
import com.commercetools.importapi.models.importsummaries.ImportSummary;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class ImportService {

    final ProjectApiRoot apiRoot;

    public ImportService(final ProjectApiRoot apiRoot) {
        this.apiRoot = apiRoot;
    }

    public CompletableFuture<ApiHttpResponse<ImportContainer>> createImportContainer(final String containerKey) {

        return
            apiRoot
                .importContainers()
                .post(importContainerDraftBuilder -> importContainerDraftBuilder.key(containerKey))
                .execute();
        }


    public CompletableFuture<ApiHttpResponse<ImportResponse>> importCustomersFromCsv(
        final String containerKey,
        final String csvFile) {

        return
                apiRoot
                        .customers()
                        .importContainers()
                        .withImportContainerKeyValue(containerKey)
                        .post(
                                priceImportRequestBuilder -> priceImportRequestBuilder
                                        .resources(getCustomersImportFromCsv(csvFile))
                        )
                        .execute();
    }

    private List<CustomerImport> getCustomersImportFromCsv(final String csvFile) {
        List<CustomerImport> customerImports = new ArrayList<>();

        try {
            InputStreamReader ioStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(csvFile));
            BufferedReader br = new BufferedReader(ioStreamReader);
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                List<String> values = Arrays.asList(line.split(","));
                CustomerImport customerImport = CustomerImportBuilder.of()
                        .key(values.get(0))
                        .email(values.get(1))
                        .password(values.get(2))
                        .firstName(values.get(3))
                        .lastName(values.get(4))
                        .isEmailVerified(Boolean.valueOf(values.get(5)))
                        .addresses(
                                CustomerAddressBuilder.of()
                                        .firstName(values.get(3))
                                        .lastName(values.get(4))
                                        .key(values.get(0) + "-home")
                                        .country(values.get(6))
                                        .build()
                        )
                        .stores(StoreKeyReferenceBuilder.of().key(values.get(7)).build())
                        .build();
                customerImports.add(customerImport);
            }
            ioStreamReader.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
        return customerImports;
    }

    public CompletableFuture<ApiHttpResponse<ImportSummary>> getImportContainerSummary(final String containerKey) {
        return
                apiRoot
                        .importContainers()
                        .withImportContainerKeyValue(containerKey)
                        .importSummaries()
                        .get()
                        .execute();
    }
}
