package handson.graphql;

import io.aexp.nodes.graphql.annotations.GraphQLArgument;
import io.aexp.nodes.graphql.annotations.GraphQLProperty;

import java.util.List;

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
