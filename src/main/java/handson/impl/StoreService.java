package handson.impl;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.graph_ql.GraphQLResponse;
import com.commercetools.api.models.product_selection.ProductsInStorePagedQueryResponse;
import com.commercetools.api.models.store.Store;
import com.commercetools.graphql.api.GraphQL;
import com.commercetools.graphql.api.GraphQLRequest;
import com.commercetools.graphql.api.types.ProductAssignmentQueryResult;
import io.vrap.rmf.base.client.ApiHttpResponse;

import java.util.concurrent.CompletableFuture;

/**

 */
public class StoreService {

    final ProjectApiRoot apiRoot;
    final String storeKey;

    public StoreService(final ProjectApiRoot apiRoot, final String storeKey) {
        this.apiRoot = apiRoot;
        this.storeKey = storeKey;
    }

    /**
     * Gets a store by key.
     * @return the store completion stage
     */
    public CompletableFuture<ApiHttpResponse<Store>> getCurrentStore() {
        return
                apiRoot
                        .stores()
                        .withKey(storeKey)
                        .get()
                        .execute();
    }

    /**
     * Creates a store by key.
     *
     * @return the store completion stage
     */
    public CompletableFuture<ApiHttpResponse<Store>> createStore() {
            return apiRoot
                    .stores()
                    .post(storeDraftBuilder -> storeDraftBuilder.key(storeKey))
                    .execute();
    }

    /**
     * Adds a product selection to a Store.
     * @return the product selection completion stage
     */
    public CompletableFuture<ApiHttpResponse<Store>> addProductSelectionToCurrentStore(
            final String productSelectionKey) {

        return
//                getCurrentStore().thenComposeAsync(storeApiHttpResponse ->
                        apiRoot
                                .stores()
                                .withKey(storeKey)
                                .post(
                                        storeUpdateBuilder -> storeUpdateBuilder
                                                .version(1L)
                                                .plusActions(
                                                        storeUpdateActionBuilder -> storeUpdateActionBuilder.addProductSelectionBuilder()
                                                                .productSelection(productSelectionResourceIdentifierBuilder -> productSelectionResourceIdentifierBuilder.key(productSelectionKey))
                                                                .active(true)
                                                )
                                )
                                .execute()
                ;
    }

    public CompletableFuture<ApiHttpResponse<ProductsInStorePagedQueryResponse>> getProductsInCurrentStore() {

        return
                apiRoot
                        .inStore(storeKey)
                        .productSelectionAssignments()
                        .get()
                        .addExpand("product")
                        .addExpand("productSelection")
                        .execute();
    }

    public CompletableFuture<ApiHttpResponse<GraphQLResponse>> getProductsInStore(final String storeKey) {

        GraphQLRequest<ProductAssignmentQueryResult> queryResultGraphQLRequest=
        GraphQL
                .productSelectionAssignments(query -> query.queryName("assignments"))
                .projection(root -> root.results().product().key());

        return  apiRoot.graphql()
                .post(queryResultGraphQLRequest)
                .execute();
    }

}
