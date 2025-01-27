package Product.PriceAnomalyDetection.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemPriceRequest {
    @NotNull
    @EqualsAndHashCode.Include
    private String item_id;

    @NotNull
    @DecimalMin(value = "0.01")
    @EqualsAndHashCode.Include
    private BigDecimal price;

}
