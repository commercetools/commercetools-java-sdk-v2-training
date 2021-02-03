package handson.impl;

public class PrefixHelper {

    // TODO:
    //  Provide your Api client prefixes as you create them. For example "mh-dev-admin." and "mh-test-admin."

    // Dev Admin Client
    private static String apiDevClientPrefix = "mh-dev-admin.";

    // Test Admin Client
    private static String apiTestClientPrefix = "TEST_PREFIX";

    // Dev Import Client
    private static String apiDevImportPrefix = "IMPORT_PREFIX";

    // Store Client Prefix
    private static String storeApiClientPrefix = "STORE_PREFIX";

    // Me Client Prefix
    private static String meApiClientPrefix = "ME_PREFIX";


    public static String getDevApiClientPrefix() {
        return apiDevClientPrefix;
    }

    public static String getTestApiClientPrefix() {
        return apiTestClientPrefix;
    }

    public static String getDevImportClientPrefix() {
        return apiDevImportPrefix;
    }

    public static String getStoreApiClientPrefix() { return storeApiClientPrefix; }

    public static String getMeApiClientPrefix() {
        return meApiClientPrefix;
    }

}
