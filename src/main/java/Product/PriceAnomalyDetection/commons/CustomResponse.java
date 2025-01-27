package Product.PriceAnomalyDetection.commons;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code","message","dateTime"})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse {

    private String code;
    private String message;
    private String dateTime;

    public String getDateTime() {
        OffsetDateTime now = OffsetDateTime.now();
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now);
    }


}
