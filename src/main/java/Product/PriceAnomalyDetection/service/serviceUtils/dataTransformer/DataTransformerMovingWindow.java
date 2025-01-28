package Product.PriceAnomalyDetection.service.serviceUtils.dataTransformer;

import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

import static Product.PriceAnomalyDetection.service.serviceUtils.commons.Commons.calculateSMA;
import static Product.PriceAnomalyDetection.service.serviceUtils.commons.Commons.calculateStandardDeviation;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataTransformerMovingWindow implements DataTransformer {
    @Value("${algorithm.window-size}")
    private int windowSize;
    @Value("${algorithm.k-value}")
    private BigDecimal kValue;
    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);

    /**
     * Transforms a list of price data into a {@link Product} object, classifying the price data into outliers and non-outliers.
     *
     * <p>This method takes an identifier and a list of {@link PriceData} objects, sorts the prices by date,
     * and classifies the prices into outliers and non-outliers based on a statistical outlier detection method.</p>
     *
     * <p>The method performs the following steps:
     * <ol>
     *     <li>If the price data list is empty or null, a new product is created with no outliers or non-outliers.</li>
     *     <li>If the price data list contains only one item, that item is classified as a non-outlier.</li>
     *     <li>If the price data list contains multiple items, the list is sorted by date, and outliers are detected
     *         based on the sorted prices.</li>
     *     <li>The outliers and non-outliers are separated and assigned to the corresponding fields of the new product.</li>
     * </ol></p>
     *
     * @param id The identifier for the product to be created.
     * @param priceDataList The list of price data to be processed and transformed into a product.
     * @return A {@link Product} object containing the id, outliers, and non-outliers based on the given price data.
     */
    @Override
    public Product transformToProduct(String id, List<PriceData> priceDataList) {

        if (priceDataList == null || priceDataList.isEmpty()) {
            Product product = new Product();
            product.setId(id);
            product.setOutliers(new ArrayList<PriceData>());
            product.setNonOutliers(new ArrayList<PriceData>());
            return product;
        }

        if(priceDataList.size() == 1){
            Product product = new Product();
            product.setId(id);
            product.setOutliers(new ArrayList<PriceData>());
            product.setNonOutliers(new ArrayList<PriceData>(priceDataList));
            return product;
        }


        priceDataList.sort(Comparator.comparing(PriceData::getDate));
        List<BigDecimal> sortedPrices = new ArrayList<>();
        for (PriceData priceData : priceDataList) {
            sortedPrices.add(priceData.getPrice());
        }

        Set<Integer> outliersIndexes = detectOutliers(sortedPrices);


        Product product = new Product();
        product.setId(id);
        List<PriceData> outliers = new ArrayList<>();
        List<PriceData> nonOutliers = new ArrayList<>();

        for (int i = 0; i < priceDataList.size(); i++) {
            if (outliersIndexes.contains(i)) {
                outliers.add(priceDataList.get(i));
            } else {
                nonOutliers.add(priceDataList.get(i));
            }
        }

        product.setOutliers(outliers);
        product.setNonOutliers(nonOutliers);
        return product;
    }

    /**
     * Detects outliers in a list of prices based on a statistical method using a sliding window.
     *
     * <p>This method iterates through the list of prices and calculates the Simple Moving Average (SMA) and
     * standard deviation for a sliding window of prices around each price. If a price is outside the calculated
     * lower and upper limits (based on the SMA and standard deviation), it is considered an outlier.</p>
     *
     * <p>The method performs the following steps for each price:
     * <ol>
     *     <li>Defines a sliding window of prices around the current price.</li>
     *     <li>Calculates the SMA and standard deviation for the window.</li>
     *     <li>Calculates the lower and upper limits using the SMA and standard deviation, adjusted by a factor defined by {@code kValue}.</li>
     *     <li>Checks if the current price is outside the calculated limits. If so, it is classified as an outlier.</li>
     * </ol></p>
     *
     * @param prices A list of prices to evaluate for outliers.
     * @return A set of indices of the prices in the list that are considered outliers.
     */
    public Set<Integer> detectOutliers(List<BigDecimal> prices) {
        Set<Integer> outlierIndices = new HashSet<>();

        for (int i = 0; i < prices.size(); i++) {

            List<BigDecimal> window = this.getWindow(i, prices);


            BigDecimal sma = calculateSMA(window);
            BigDecimal stdDev = calculateStandardDeviation(window, sma);


            BigDecimal lowerLimit = sma.subtract(kValue.multiply(stdDev, MATH_CONTEXT), MATH_CONTEXT);
            BigDecimal upperLimit = sma.add(kValue.multiply(stdDev, MATH_CONTEXT), MATH_CONTEXT);


            BigDecimal currentPrice = prices.get(i);

            if (currentPrice.compareTo(lowerLimit) < 0 || currentPrice.compareTo(upperLimit) > 0) {
                outlierIndices.add(i);
            }
        }
        return outlierIndices;
    }

    private List<BigDecimal> getWindow(int i, List<BigDecimal> prices) {

        int half = windowSize / 2;
        int start = Math.max(0, i - half);
        int end = Math.min(prices.size(), i + half + 1);
        List<BigDecimal> window = new ArrayList<>();

        for (int j = start; j < end; j++) {
            if (j != i) {
                window.add(prices.get(j));
            }
        }
        return window;
    }


}