package handson.impl;

public enum ApiPrefixHelper {
    API_DEV_CLIENT_PREFIX("ctp."),
    API_TEST_CLIENT_PREFIX("ctp-test."),
    API_DEV_IMPORT_PREFIX("ctp-import."),
    API_ME_CLIENT_PREFIX("ctp-me.");

    private final String prefix;

    ApiPrefixHelper(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}