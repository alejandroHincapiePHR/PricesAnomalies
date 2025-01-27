package Product.PriceAnomalyDetection.service.serviceUtils.dataTransformer;

import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.model.Product;
import java.math.BigDecimal;
import java.util.List;

public interface DataTransformer {

    Product transformToProduct(String id, List<PriceData> priceDataList);

}
