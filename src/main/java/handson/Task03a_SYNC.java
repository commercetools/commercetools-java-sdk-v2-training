package handson;

/*
import com.commercetools.sync.products.ProductSync;
import com.commercetools.sync.products.ProductSyncOptions;
import com.commercetools.sync.products.ProductSyncOptionsBuilder;
import handson.impl.ClientService;
import io.sphere.sdk.client.SphereAccessTokenSupplier;
import io.sphere.sdk.client.SphereAsyncHttpClientFactory;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.http.HttpClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.products.*;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.utils.MoneyImpl;


import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.lang.String.format;
*/

public class Task03a_SYNC {

    public static void main(String[] args) {

    }

}



/*

        // Step 1: Add dependencies for old sdk and sync library
        gradle.build
        implementation 'com.commercetools:commercetools-sync-java:1.9.1'
        implementation 'com.commercetools.sdk.jvm.core:commercetools-models:1.53.0'
        implementation 'com.commercetools.sdk.jvm.core:commercetools-java-client:1.53.0'

        // Step 2: Make sure the authUrl is set as follows, scopes are commented out
        dev.properties
        mh-sync-admin.authUrl=https://auth.europe-west1.gcp.commercetools.com
        ## mh-sync-admin.scopes=manage_project:training-011-avensia-test

        // Step 3: Sync
        a) Create SphereCliebt
        b) Create Sync Library
        c) Sync a list of product drafts

     public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        java.util.logging.Logger logger = Logger.getLogger(Task02b_UPDATE_Group.class.getName());
        
        // old SphereClient
        final Properties prop = new Properties();
        prop.load(ClientService.class.getResourceAsStream("/dev.properties"));
        final SphereClientConfig sphereClientConfig = SphereClientConfig.ofProperties(prop, "mh-sync-admin.");
        final HttpClient httpClient = new SphereAsyncHttpClientFactory().getClient();
        final SphereAccessTokenSupplier sphereAccessTokenSupplier =
                SphereAccessTokenSupplier.ofAutoRefresh(sphereClientConfig, httpClient, true);
        final SphereClient sphereClient = SphereClient.of(sphereClientConfig, httpClient, sphereAccessTokenSupplier);

        final String inputFilePath = "/products.csv";
        final List<ProductDraft> productDrafts = processInputFile(inputFilePath);

        logger.info("Parsed " + inputFilePath + productDrafts);
        logger.info("Starting Sync..");

        // TODO Sync products
        final ProductSync productSync = new ProductSync(
                                                    ProductSyncOptionsBuilder.of(sphereClient)
                                                        .errorCallback((s, throwable) -> {logger.info(s + throwable.getMessage());})
                                                        .warningCallback(message -> {logger.info(message);})
                                                        .build()
                                                );
        productSync.sync(productDrafts)
                    .thenAcceptAsync(productSyncStatistics -> logger.info(productSyncStatistics.getReportMessage()))
                    .toCompletableFuture()
                    .get();


    }

    private static List<io.sphere.sdk.products.ProductDraft> processInputFile(@Nonnull final String inputFilePath) {
        final InputStream csvAsStream = Task03a_SYNC.class.getResourceAsStream(inputFilePath);
        final BufferedReader br = new BufferedReader(new InputStreamReader(csvAsStream));

        return br.lines()
                 .skip(1) // skip the header of the csv
                 .map(Task03a_SYNC::processLine)
                 .collect(Collectors.toList());
    }


    private static io.sphere.sdk.products.ProductDraft processLine(@Nonnull final String line) {
        final String[] splitLine = line.split(",");

        //TODO Please replace the prefix below (with value "yourName") with your actual name.
        final String prefix = "MH";
        final String productTypeKey = splitLine[0];
        final String productKey = format("%s-%s", prefix, splitLine[1]);
        final String sku = format("%s-%s", prefix, splitLine[2]);
        final String variantKey = format("%s-%s", prefix, splitLine[3]);
        final String productName = format("%s-%s", prefix, splitLine[4]);
        final String productDescription = splitLine[5];
        final double basePrice = Double.parseDouble(splitLine[6]);
        final String currencyCode = splitLine[7];
        final String imageUrl = splitLine[8];


        final PriceDraft priceDraft = PriceDraftBuilder
            .of(MoneyImpl.of(BigDecimal.valueOf(basePrice), currencyCode))
            .build();

        final Image image = Image.of(imageUrl, ImageDimensions.of(100, 100));

        //TODO Create a ProductVariantDraft.
        final ProductVariantDraft productVariantDraft = ProductVariantDraftBuilder.of()
                .sku(sku)
                .key(variantKey)
                .prices(priceDraft)
                .images(image)
                .build();

        //TODO Create a ProductDraft and return it.
        final io.sphere.sdk.products.ProductDraft productDraft = ProductDraftBuilder.of(ProductType.reference(productTypeKey),
                LocalizedString.ofEnglish(productName),
                LocalizedString.ofEnglish(sku),
                productVariantDraft)
                .key(productKey)
                .description(LocalizedString.ofEnglish(productDescription))
                .build();

        return productDraft;
    }
*/