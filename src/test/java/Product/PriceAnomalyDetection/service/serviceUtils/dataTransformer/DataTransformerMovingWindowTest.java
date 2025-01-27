package Product.PriceAnomalyDetection.service.serviceUtils.dataTransformer;

import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.service.serviceUtils.anomalyDetection.AnomalyDetectionStandardDeviation;
import Product.PriceAnomalyDetection.service.serviceUtils.commons.Commons;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataTransformerMovingWindowTest {

    @InjectMocks
    private DataTransformerMovingWindow dataTransformer;

    @InjectMocks
    private Commons commons;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dataTransformer.setKValue(BigDecimal.valueOf(2));
        dataTransformer.setWindowSize(10);
    }

    @Test
    public void testTransformToProduct() {

        String id = "test-product";
        List<PriceData> priceDataList = Arrays.asList(
                new PriceData(new Date(), new BigDecimal("116")),
                new PriceData(new Date(), new BigDecimal("116")),
                new PriceData(new Date(), new BigDecimal("109")),
                new PriceData(new Date(), new BigDecimal("200")),
                new PriceData(new Date(), new BigDecimal("110")),
                new PriceData(new Date(), new BigDecimal("115")),
                new PriceData(new Date(), new BigDecimal("110")),
                new PriceData(new Date(), new BigDecimal("1100")),
                new PriceData(new Date(), new BigDecimal("115")),
                new PriceData(new Date(), new BigDecimal("110")),
                new PriceData(new Date(), new BigDecimal("115")),
                new PriceData(new Date(), new BigDecimal("110")),
                new PriceData(new Date(), new BigDecimal("116")),
                new PriceData(new Date(), new BigDecimal("20")),
                new PriceData(new Date(), new BigDecimal("130"))

        );

        Product product = dataTransformer.transformToProduct(id, priceDataList);
        System.out.println(product);
        assertNotNull(product);
        assertEquals(id, product.getId());


        assertEquals(11, product.getNonOutliers().size());
        assertEquals(4, product.getOutliers().size());
    }

    @Test
    public void testDetectOutliers() {

        List<BigDecimal> prices = Arrays.asList(
                new BigDecimal("100"),
                new BigDecimal("105"),
                new BigDecimal("110"),
                new BigDecimal("200") // Outlier
        );

        Set<Integer> outlierIndices = dataTransformer.detectOutliers(prices);

        assertEquals(1, outlierIndices.size()); // Un outlier
        assertTrue(outlierIndices.contains(3)); // El outlier está en la posición 3
    }



    @Test
    public void testCalculateSMA() {
        List<BigDecimal> prices = Arrays.asList(
                new BigDecimal("100"),
                new BigDecimal("105"),
                new BigDecimal("110")
        );


        BigDecimal sma = Commons.calculateSMA(prices);


        assertEquals(new BigDecimal("105"), sma); // Media: (100 + 105 + 110) / 3 = 105
    }


    @Test
    public void testCalculateStandardDeviation() {
        List<BigDecimal> prices = Arrays.asList(
                new BigDecimal("100"),
                new BigDecimal("105"),
                new BigDecimal("110")
        );
        BigDecimal sma = new BigDecimal("105");

        BigDecimal stdDev = Commons.calculateStandardDeviation(prices, sma);

        assertTrue(stdDev.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void testTransformToProduct_EmptyList() {

        String id = "test-product";
        List<PriceData> priceDataList = List.of();

        Product product = dataTransformer.transformToProduct(id, priceDataList);

        assertNotNull(product);
        assertEquals(id, product.getId());
        assertTrue(product.getOutliers().isEmpty());
        assertTrue(product.getNonOutliers().isEmpty());
    }


    @Test
    public void testTransformToProduct_SingleElement() {
        String id = "test-product";
        List<PriceData> priceDataList = List.of(
                new PriceData(new Date(), new BigDecimal("100"))
        );

        Product product = dataTransformer.transformToProduct(id, priceDataList);

        assertNotNull(product);
        assertEquals(id, product.getId());
        assertTrue(product.getOutliers().isEmpty());
        assertEquals(1, product.getNonOutliers().size());
    }


}
