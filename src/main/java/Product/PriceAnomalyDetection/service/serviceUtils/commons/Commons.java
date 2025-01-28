package Product.PriceAnomalyDetection.service.serviceUtils.commons;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class Commons {
    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);

    /**
     * Calculates the Simple Moving Average (SMA) of a list of prices, excluding outliers.
     *
     * <p>This method first filters out outlier prices from the input list, then computes the average
     * of the remaining prices. If there are no prices left after filtering out the outliers, it returns zero.</p>
     *
     * <p>The calculation steps are as follows:
     * <ol>
     *     <li>Removes outlier prices from the input list using the {@link #removeOutliers(List)} method.</li>
     *     <li>If there are no remaining prices, returns {@link BigDecimal#ZERO}.</li>
     *     <li>Calculates the sum of the remaining prices.</li>
     *     <li>Divides the sum by the number of remaining prices to compute the average (SMA), using the
     *         {@link #MATH_CONTEXT} for precision control.</li>
     * </ol></p>
     *
     * @param prices A list of prices to calculate the SMA for, with potential outliers removed.
     * @return The Simple Moving Average (SMA) of the filtered prices, or {@link BigDecimal#ZERO} if no prices remain.
     */
    public static BigDecimal calculateSMA(List<BigDecimal> prices) {
        List<BigDecimal> filteredPrices = removeOutliers(prices);
        if (filteredPrices.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = filteredPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(filteredPrices.size()), MATH_CONTEXT);
    }

    /**
     * Calculates the standard deviation of a list of prices, excluding outliers.
     *
     * <p>This method first filters out outlier prices from the input list, then calculates the variance
     * of the remaining prices. Finally, the square root of the variance is computed to obtain the standard deviation.</p>
     *
     * <p>The calculation steps are as follows:
     * <ol>
     *     <li>Removes outlier prices from the input list using the {@link #removeOutliers(List)} method.</li>
     *     <li>If no prices remain after filtering, returns {@link BigDecimal#ZERO}.</li>
     *     <li>For each remaining price, computes the squared difference from the provided mean and adds it to the variance.</li>
     *     <li>Divides the accumulated variance by the number of remaining prices to compute the average variance.</li>
     *     <li>Returns the square root of the variance as the standard deviation, using the {@link #MATH_CONTEXT} for precision control.</li>
     * </ol></p>
     *
     * @param prices A list of prices to calculate the standard deviation for, with potential outliers removed.
     * @param mean The mean (average) of the prices used to compute the variance.
     * @return The standard deviation of the filtered prices, or {@link BigDecimal#ZERO} if no prices remain.
     */

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

    /**
     * Removes outliers from a list of prices based on the Interquartile Range (IQR) method.
     *
     * <p>This method filters out prices that fall outside the lower and upper bounds,
     * which are determined by the Interquartile Range (IQR). The lower and upper bounds
     * are calculated as 1.5 times the IQR below the first quartile (Q1) and above the third quartile (Q3), respectively.</p>
     *
     * <p>The steps of the method are as follows:
     * <ol>
     *     <li>Sorts the list of prices in ascending order.</li>
     *     <li>Calculates the first quartile (Q1) and third quartile (Q3) of the sorted prices.</li>
     *     <li>Calculates the IQR as the difference between Q3 and Q1.</li>
     *     <li>Determines the lower and upper bounds based on the IQR, using the formula:
     *         <ul>
     *             <li>Lower bound = Q1 - 1.5 * IQR</li>
     *             <li>Upper bound = Q3 + 1.5 * IQR</li>
     *         </ul>
     *     </li>
     *     <li>Filters out prices that fall outside the lower and upper bounds.</li>
     * </ol></p>
     *
     * @param prices A list of prices to be filtered for outliers.
     * @return A list of prices that are within the calculated bounds, with outliers removed.
     */
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

    /**
     * Calculates a specific quartile (e.g., Q1, Q3) from a sorted list of prices based on the specified percentile.
     *
     * <p>This method computes the quartile value for a given percentile (e.g., 0.25 for the first quartile, 0.75 for the third quartile)
     * in a sorted list of prices. It uses linear interpolation between the two closest values if the exact percentile does not
     * correspond to an integer index in the sorted list.</p>
     *
     * <p>The calculation process is as follows:
     * <ol>
     *     <li>Determines the index corresponding to the specified percentile.</li>
     *     <li>If the index is an integer, the value at that index in the sorted list is returned.</li>
     *     <li>If the index is not an integer, the method interpolates between the two closest values in the list.</li>
     * </ol></p>
     *
     * @param sortedPrices A sorted list of prices.
     * @param percentile The percentile (between 0 and 1) for which to calculate the quartile (e.g., 0.25 for Q1, 0.75 for Q3).
     * @return The value corresponding to the specified percentile in the sorted list of prices, using interpolation if necessary.
     */
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
