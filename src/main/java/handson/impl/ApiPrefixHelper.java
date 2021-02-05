package handson.impl;

public enum ApiPrefixHelper {
    API_DEV_CLIENT_PREFIX("DEV_PREFIX"),
    API_TEST_CLIENT_PREFIX("TEST_PREFIX"),
    API_DEV_IMPORT_PREFIX("DEV_IMPORT_PREFIX"),
    API_STORE_CLIENT_PREFIX("DEV_STORE_PREFIX"),
    API_ME_CLIENT_PREFIX("DEV_ME_PREFIX");

    private final String prefix;

    private ApiPrefixHelper(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}
