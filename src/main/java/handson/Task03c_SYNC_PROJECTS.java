package handson;

import handson.impl.ApiPrefixHelper;
import handson.impl.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;



public class Task03c_SYNC_PROJECTS {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(Task02b_UPDATE_Group.class.getName());

        // TODO
        //  Have docker installed
        //  Make sure, source and target project have proper setup (locales, countries, taxes...)
        //
        String sourcePrefix = ApiPrefixHelper.API_DEV_CLIENT_PREFIX.getPrefix();     // Your source api client prefix
        String targetPrefix = ApiPrefixHelper.API_TEST_CLIENT_PREFIX.getPrefix();    // Your target api client prefix

        Properties properties = new Properties();
        properties.load(ClientService.class.getResourceAsStream("/dev.properties"));
        StringBuilder dockerRun = new StringBuilder();
        dockerRun.append("docker run ");
        dockerRun.append(" -e SOURCE_PROJECT_KEY=" + properties.getProperty(sourcePrefix + "projectKey"));
        dockerRun.append(" -e SOURCE_CLIENT_ID=" + properties.getProperty(sourcePrefix + "clientId"));
        dockerRun.append(" -e SOURCE_CLIENT_SECRET=" + properties.getProperty(sourcePrefix + "clientSecret"));
        dockerRun.append(" -e TARGET_PROJECT_KEY=" + properties.getProperty(targetPrefix + "projectKey"));
        dockerRun.append(" -e TARGET_CLIENT_ID=" + properties.getProperty(targetPrefix + "clientId"));
        dockerRun.append(" -e TARGET_CLIENT_SECRET=" + properties.getProperty(targetPrefix + "clientSecret"));


        // TODO
        //  Modify as wished
        //  RUN the project sync
        //
        dockerRun.append(" commercetools/commercetools-project-sync:3.8.0 -s all");
        logger.info(dockerRun.toString());

        Process process = Runtime.getRuntime().exec(dockerRun.toString());
        process.waitFor();
        logger.info(process.exitValue() + " ");

    }
}

