package Product.PriceAnomalyDetection.service;

import Product.PriceAnomalyDetection.model.Product;

import java.math.BigDecimal;

public interface IProductService extends IGenericService<Product, String>{

    Boolean isAnomaly(String id, BigDecimal price);
}
