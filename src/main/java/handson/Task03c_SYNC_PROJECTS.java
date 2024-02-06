package handson;

import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;



public class Task03c_SYNC_PROJECTS {

    public static void main(String[] args) throws IOException, InterruptedException {

        Logger logger = LoggerFactory.getLogger("commercetools");

        // TODO
        //  Have docker installed
        //  Provide here source and target project prefixes in "dev.properties"
        //  Make sure, source and target project have proper setup (locales, countries, taxes...)
        //

        final String sourcePrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();     // Your source api client prefix
        final String targetPrefix = ApiPrefixHelper.API_TEST_CLIENT_PREFIX.getPrefix();    // Your target api client prefix

        Properties properties = new Properties();
        properties.load(ClientService.class.getResourceAsStream("/dev.properties"));
        StringBuilder dockerRun = new StringBuilder();
        dockerRun.append("docker run ");
        dockerRun.append(" -e SOURCE_PROJECT_KEY=" + properties.getProperty(sourcePrefix + "projectKey"));
        dockerRun.append(" -e SOURCE_CLIENT_ID=" + properties.getProperty(sourcePrefix + "clientId"));
        dockerRun.append(" -e SOURCE_CLIENT_SECRET=" + properties.getProperty(sourcePrefix + "clientSecret"));
        dockerRun.append(" -e SOURCE_AUTH_URL=https://auth.europe-west1.gcp.commercetools.com");
        dockerRun.append(" -e SOURCE_API_URL=https://api.europe-west1.gcp.commercetools.com");
        dockerRun.append(" -e TARGET_PROJECT_KEY=" + properties.getProperty(targetPrefix + "projectKey"));
        dockerRun.append(" -e TARGET_CLIENT_ID=" + properties.getProperty(targetPrefix + "clientId"));
        dockerRun.append(" -e TARGET_CLIENT_SECRET=" + properties.getProperty(targetPrefix + "clientSecret"));
        dockerRun.append(" -e TARGET_AUTH_URL=https://auth.europe-west1.gcp.commercetools.com");
        dockerRun.append(" -e TARGET_API_URL=https://api.europe-west1.gcp.commercetools.com");

        // TODO
        //  Modify as wished
        //  RUN the project sync
        //
        dockerRun.append(" commercetools/commercetools-project-sync:5.3.1 -s all");
        logger.info(dockerRun.toString());

        Process process = Runtime.getRuntime().exec(dockerRun.toString());
        process.waitFor();
        logger.info(process.exitValue() + " ");

    }
}

