package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedStringBuilder;
import com.commercetools.api.models.product_selection.ProductSelection;
import com.commercetools.api.models.product_selection.ProductSelectionProductPagedQueryResponse;
import io.vrap.rmf.base.client.ApiHttpResponse;
import jdk.jshell.spi.ExecutionControl;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**

 */
public class ProductSelectionService {

    final ProjectApiRoot apiRoot;

    public ProductSelectionService(final ProjectApiRoot client) {
        this.apiRoot = client;
    }


    /**
     * Gets a product selection by key.
     *
     * @return the product selection completion stage
     */
    public CompletableFuture<ApiHttpResponse<ProductSelection>> getProductSelectionByKey(final String productSelectionKey) {
        return
            apiRoot
                .productSelections()
                .withKey(productSelectionKey)
                .get()
                .execute();
    }

    /**
     * Creates a new product selection.
     *
     * @return the product selection creation completion stage
     */
    public CompletableFuture<ApiHttpResponse<ProductSelection>> createProductSelection(final String productSelectionKey, Map<String, String> psName) {

        return null;
    }


    public CompletableFuture<ApiHttpResponse<ProductSelection>> addProductToProductSelection(
        final ApiHttpResponse<ProductSelection> productSelectionApiHttpResponse,
        final String productKey) {

        final ProductSelection productSelection = productSelectionApiHttpResponse.getBody();
        return null;
    }



    public CompletableFuture<ApiHttpResponse<ProductSelectionProductPagedQueryResponse>> getProductsInProductSelection(
        final String productSelectionKey) {

        return null;
    }

}
