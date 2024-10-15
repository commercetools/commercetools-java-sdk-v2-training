package handson.graphql;

import io.vrap.rmf.base.client.ApiHttpResponse;

public class ProductResponseMapper {

    public static ProductResponse mapFromGraphQLResponse(ApiHttpResponse<ProductResponse> response) {
        if (response == null || response.getBody() == null) {
            return null;
        }

        ProductResponse productResponse = response.getBody();

        return productResponse;
    }
    public static int getProductTotal(ApiHttpResponse<ProductResponse> response) {
        if (response == null || response.getBody() == null) {
            return 0;
        }

        int total = response.getBody().getProducts().getTotal();

        return total;
    }
}
