package Product.PriceAnomalyDetection.commons;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"api","method","code","message","dateTime","response"})
public class CustomResponse {
    private String code;
    private String message;
    private String dateTime;
    private Object response;

    public String getDateTime() {
        OffsetDateTime now = OffsetDateTime.now();
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now);
    }
}
