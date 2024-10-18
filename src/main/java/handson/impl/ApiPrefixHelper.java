package handson.impl;

public enum ApiPrefixHelper {
    API_DEV_CLIENT_PREFIX("ctp."),
    API_STORE_CLIENT_PREFIX("ctp-store."),
    API_TEST_CLIENT_PREFIX("ctp-test.");


    private final String prefix;

    ApiPrefixHelper(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}