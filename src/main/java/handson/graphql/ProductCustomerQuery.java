package handson.graphql;

import io.aexp.nodes.graphql.annotations.GraphQLArgument;
import io.aexp.nodes.graphql.annotations.GraphQLProperty;

public class ProductCustomerQuery {

    @GraphQLProperty(name="products", arguments = { @GraphQLArgument(name = "limit", type = "Integer"), @GraphQLArgument(name = "sort", type = "String") })
    Products products;
    Customers customers;

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }

}
