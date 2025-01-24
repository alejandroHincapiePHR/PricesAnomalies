package Product.PriceAnomalyDetection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ItemPriceRequest {
    private String item_id;
    private BigDecimal price;

}
