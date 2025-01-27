package Product.PriceAnomalyDetection.service.serviceUtils.anomalyDetection;

import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.service.serviceUtils.anomalyDetection.AnomalyDetectionStandardDeviation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AnomalyDetectionStandardDeviationTest {
    @InjectMocks
    private AnomalyDetectionStandardDeviation anomalyDetection;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        anomalyDetection.setKValue(BigDecimal.valueOf(2));
        anomalyDetection.setWindowSize(3);
    }


    @Test
    public void testIsAnomaly_NewPriceIsAnomaly() {
        List<PriceData> nonOutliers = Arrays.asList(
                new PriceData(new Date(), new BigDecimal("100")),
                new PriceData(new Date(), new BigDecimal("105")),
                new PriceData(new Date(), new BigDecimal("110"))
        );
        BigDecimal newPrice = new BigDecimal("150");
        String result = anomalyDetection.isAnomaly(nonOutliers, newPrice);
        assertEquals("true", result);
    }

    @Test
    public void testIsAnomaly_NewPriceIsNotAnomaly() {
        List<PriceData> nonOutliers = Arrays.asList(
                new PriceData(new Date(), new BigDecimal("100")),
                new PriceData(new Date(), new BigDecimal("105")),
                new PriceData(new Date(), new BigDecimal("110"))
        );
        BigDecimal newPrice = new BigDecimal("106");
        String result = anomalyDetection.isAnomaly(nonOutliers, newPrice);
        assertEquals("false", result);
    }

    @Test
    public void testIsAnomaly_NonOutliersSizeLessThanWindowSize() {

        List<PriceData> nonOutliers = Arrays.asList(
                new PriceData(new Date(), new BigDecimal("100")),
                new PriceData(new Date(), new BigDecimal("105"))
        );
        BigDecimal newPrice = new BigDecimal("110");

        String result = anomalyDetection.isAnomaly(nonOutliers, newPrice);
        assertNotNull(result);
    }

    @Test
    public void testIsAnomaly_NonOutliersEmpty() {

        List<PriceData> nonOutliers = List.of();
        BigDecimal newPrice = new BigDecimal("100");
        String result = anomalyDetection.isAnomaly(nonOutliers, newPrice);

        assertNotNull(result);
    }

}
