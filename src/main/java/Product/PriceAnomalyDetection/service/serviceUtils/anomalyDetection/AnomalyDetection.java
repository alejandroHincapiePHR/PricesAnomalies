package Product.PriceAnomalyDetection.service.serviceUtils.anomalyDetection;

import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface AnomalyDetection {
    String isAnomaly(List<PriceData> nonOutliers, BigDecimal price);
}
