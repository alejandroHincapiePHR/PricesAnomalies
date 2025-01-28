package Product.PriceAnomalyDetection.service.serviceUtils.anomalyDetection;

import Product.PriceAnomalyDetection.model.PriceData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static Product.PriceAnomalyDetection.service.serviceUtils.commons.Commons.calculateSMA;
import static Product.PriceAnomalyDetection.service.serviceUtils.commons.Commons.calculateStandardDeviation;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnomalyDetectionStandardDeviation implements AnomalyDetection {

    @Value("${algorithm.window-size}")
    private int windowSize ;
    @Value("${algorithm.k-value}")
    private  BigDecimal kValue;
    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);

    /**
     * Determines whether a given price is an anomaly based on historical non-outlier prices.
     *
     * <p>This method calculates the Simple Moving Average (SMA) and standard deviation of the most recent
     * historical non-outlier prices and compares the new price against upper and lower limits derived from
     * these values. If the new price is outside the calculated limits, it is considered an anomaly.</p>
     *
     * <p>The method follows these steps:
     * <ol>
     *     <li>Sorts the list of non-outlier prices by date.</li>
     *     <li>Selects a sliding window of the most recent prices based on a predefined window size.</li>
     *     <li>Calculates the SMA and standard deviation for the selected window of prices.</li>
     *     <li>Calculates the lower and upper limits using the SMA and standard deviation, adjusting by a factor
     *         defined by {@code kValue}.</li>
     *     <li>Compares the new price with the calculated limits to determine if it falls outside (anomaly) or inside
     *         (not an anomaly) the range.</li>
     * </ol></p>
     *
     * @param nonOutliers A list of historical price data that are not considered outliers.
     * @param newPrice The new price to evaluate for anomaly detection.
     * @return {@code "true"} if the new price is considered an anomaly, {@code "false"} otherwise.
     */
    @Override
    public String isAnomaly(List<PriceData> nonOutliers, BigDecimal newPrice) {

        if(nonOutliers.isEmpty()){
            return "false";
        }

        nonOutliers.sort(Comparator.comparing(PriceData::getDate));
        List<BigDecimal> sortedPrices = new ArrayList<>();

        for (PriceData priceData : nonOutliers) {
            sortedPrices.add(priceData.getPrice());
        }
        int start = sortedPrices.size() - (windowSize + 1);
        int end = sortedPrices.size() - 1;

        if (start < 0) {
            start = 0;
        }

        List<BigDecimal> window = sortedPrices.subList(start, end);


        BigDecimal sma = calculateSMA(window);
        BigDecimal stdDev = calculateStandardDeviation(window, sma);

        BigDecimal lowerLimit = sma.subtract(kValue.multiply(stdDev, MATH_CONTEXT), MATH_CONTEXT);
        BigDecimal upperLimit = sma.add(kValue.multiply(stdDev, MATH_CONTEXT), MATH_CONTEXT);
        Boolean result = newPrice.compareTo(lowerLimit) < 0 || newPrice.compareTo(upperLimit) > 0;
        return result.toString();

    }


}
