package Product.PriceAnomalyDetection.model;

import Product.PriceAnomalyDetection.controller.commons.CustomResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ItemPriceResponse {
    private String item_id;
    private BigDecimal price;
    private String anomaly;
    private CustomResponse metadata;
    private String status_code;
}
