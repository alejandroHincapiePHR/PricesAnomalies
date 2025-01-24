package Product.PriceAnomalyDetection.service.anomalyDetection;

import Product.PriceAnomalyDetection.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface AnomalyDetection {
    Boolean isAnomaly(List<BigDecimal> nonOutliers, BigDecimal price);
}
