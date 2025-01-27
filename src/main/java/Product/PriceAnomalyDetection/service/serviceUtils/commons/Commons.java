package Product.PriceAnomalyDetection.service.serviceUtils.commons;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class Commons {
    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);
    public static BigDecimal calculateSMA(List<BigDecimal> prices) {
        List<BigDecimal> filteredPrices = removeOutliers(prices);
        if (filteredPrices.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = filteredPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(filteredPrices.size()), MATH_CONTEXT);
    }

    public static BigDecimal calculateStandardDeviation(List<BigDecimal> prices, BigDecimal mean) {
        List<BigDecimal> filteredPrices = removeOutliers(prices);
        if (filteredPrices.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal variance = BigDecimal.ZERO;
        for (BigDecimal price : filteredPrices) {
            BigDecimal diff = price.subtract(mean, MATH_CONTEXT);
            variance = variance.add(diff.pow(2, MATH_CONTEXT), MATH_CONTEXT);
        }
        variance = variance.divide(new BigDecimal(filteredPrices.size()), MATH_CONTEXT);
        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
    }

    public static List<BigDecimal> removeOutliers(List<BigDecimal> prices) {
        if (prices == null || prices.isEmpty()) {
            return List.of();
        }
        List<BigDecimal> sortedPrices = prices.stream().sorted().collect(Collectors.toList());
        BigDecimal q1 = calculateQuartile(sortedPrices, 0.25);
        BigDecimal q3 = calculateQuartile(sortedPrices, 0.75);
        BigDecimal iqr = q3.subtract(q1);
        BigDecimal lowerBound = q1.subtract(iqr.multiply(BigDecimal.valueOf(1.5)));
        BigDecimal upperBound = q3.add(iqr.multiply(BigDecimal.valueOf(1.5)));
        return prices.stream()
                .filter(price -> price.compareTo(lowerBound) >= 0 && price.compareTo(upperBound) <= 0)
                .collect(Collectors.toList());
    }

    private static BigDecimal calculateQuartile(List<BigDecimal> sortedPrices, double percentile) {
        int size = sortedPrices.size();
        double index = percentile * (size - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);
        if (lowerIndex == upperIndex) {
            return sortedPrices.get(lowerIndex);
        }
        BigDecimal lowerValue = sortedPrices.get(lowerIndex);
        BigDecimal upperValue = sortedPrices.get(upperIndex);
        BigDecimal fraction = BigDecimal.valueOf(index - lowerIndex);
        return lowerValue.add(upperValue.subtract(lowerValue).multiply(fraction));
    }

}
