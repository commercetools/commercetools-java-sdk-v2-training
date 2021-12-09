package handson.graphql;

import io.aexp.nodes.graphql.annotations.GraphQLProperty;


@GraphQLProperty(name="customers")
public class Customers {

    private int total;

    public void setTotal(int total) {
        this.total = total;
    }
    public int getTotal() {
        return total;
    }

}
