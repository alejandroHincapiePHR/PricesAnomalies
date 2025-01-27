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
