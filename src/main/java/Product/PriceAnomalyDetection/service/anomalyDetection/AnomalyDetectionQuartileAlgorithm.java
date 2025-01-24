package Product.PriceAnomalyDetection.service.anomalyDetection;

import Product.PriceAnomalyDetection.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
public class AnomalyDetectionQuartileAlgorithm implements AnomalyDetection {
    @Override
    public Boolean isAnomaly(List<BigDecimal> nonOutliers, BigDecimal newPrice) {
        Collections.sort(nonOutliers);

        BigDecimal q1 = calculateQuartile(nonOutliers, 0.25);
        BigDecimal q3 = calculateQuartile(nonOutliers, 0.75);
        BigDecimal iqr = q3.subtract(q1);

        BigDecimal lowerLimit = q1.subtract(iqr.multiply(new BigDecimal("1.5")));
        BigDecimal upperLimit = q3.add(iqr.multiply(new BigDecimal("1.5")));

        return newPrice.compareTo(lowerLimit) < 0 || newPrice.compareTo(upperLimit) > 0;


    }


    private static BigDecimal calculateQuartile(List<BigDecimal> prices, double percentile) {
        int n = prices.size();
        double position = percentile * (n + 1);
        if (position < 1) {
            return prices.get(0);
        } else if (position >= n) {
            return prices.get(n - 1);
        } else {
            int integerPosition = (int) position;
            double fraction = position - integerPosition;

            BigDecimal lowerValue = prices.get(integerPosition - 1);
            BigDecimal upperValue = prices.get(integerPosition);

            return lowerValue.add(
                    upperValue.subtract(lowerValue).multiply(new BigDecimal(fraction))
            );
        }
    }


}
