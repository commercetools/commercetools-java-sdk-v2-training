package handson.graphql;

import io.aexp.nodes.graphql.annotations.GraphQLArgument;
import io.aexp.nodes.graphql.annotations.GraphQLArguments;
import io.aexp.nodes.graphql.annotations.GraphQLProperty;

public class Current {

    @GraphQLProperty(name="name", arguments = { @GraphQLArgument(name="locale", value = "en") })
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
