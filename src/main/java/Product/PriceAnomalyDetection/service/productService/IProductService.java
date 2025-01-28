package Product.PriceAnomalyDetection.service.productService;

import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.service.genericService.IGenericService;

import java.math.BigDecimal;

public interface IProductService extends IGenericService<Product, String> {

    String isAnomaly(String id, BigDecimal price);
}
