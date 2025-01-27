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