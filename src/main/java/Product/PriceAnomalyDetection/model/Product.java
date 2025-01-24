package Product.PriceAnomalyDetection.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "products")
@ToString
public class Product {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Field
    private List<BigDecimal> outliers;

    @Field
    private List<BigDecimal> nonOutliers;

}
