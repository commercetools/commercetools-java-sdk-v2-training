package handson;


import com.commercetools.ml.client.ApiRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSOutput;

import javax.crypto.spec.PSource;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static handson.impl.ClientService.createMLApiClient;
import static handson.impl.ClientService.getProjectKey;

public class Task08b_MACHINE_LEARNING {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String projectKey = getProjectKey("mh-ml-admin.");
        final ApiRoot client = createMLApiClient("mh-ml-admin.");
        Logger logger = LoggerFactory.getLogger(Task04b_CHECKOUT.class.getName());

            // TODO: Use the Category Recommendation API
            // Query a recommendation for a name and image
            //
            logger.info("Categories from name: ");
                    client
                            .withProjectKey(projectKey)
                            .recommendations()
                            .generalCategories()
                            .get()
                            .withProductName("black car")
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getResults().forEach(
                                    generalCategoryRecommendation ->
                                        System.out.println(
                                                "category " + generalCategoryRecommendation.getCategoryName() +
                                                                generalCategoryRecommendation.getConfidence()
                                        )
                            );

            logger.info("Categories from image: ");
                    client
                            .withProjectKey(projectKey)
                            .recommendations()
                            .generalCategories()
                            .get()
                            .withProductImageUrl("https://28b684b17fa0dcc38c87-554d6960463486a597750960d51e96de.ssl.cf3.rackcdn.com/tulip-flower-500x500-f3v0FQsD.jpg")
                            .execute()
                            .toCompletableFuture().get()
                            .getBody().getResults().forEach(
                            generalCategoryRecommendation ->
                                    System.out.println(
                                            "category " + generalCategoryRecommendation.getCategoryName() +
                                                    generalCategoryRecommendation.getConfidence()
                                    )
                    );




    }
}
