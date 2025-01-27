package Product.PriceAnomalyDetection.errorHandling;

import Product.PriceAnomalyDetection.commons.CustomResponse;
import Product.PriceAnomalyDetection.constant.MessageConstants;
import Product.PriceAnomalyDetection.errorHandling.exceptions.ProductNotFoundException;
import Product.PriceAnomalyDetection.model.ItemPriceResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.text.ParseException;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ItemPriceResponse> productNotFoundException(ProductNotFoundException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_RESPONSE_NOT_FOUND);
        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("404");
        itemPriceResponse.setMetadata(customResponse);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(itemPriceResponse);

    }


    @ExceptionHandler
    public ResponseEntity<ItemPriceResponse> MethodArgumentNotValidException(MethodArgumentNotValidException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_RESPONSE_BAD_REQUEST);
        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("400");
        itemPriceResponse.setMetadata(customResponse);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(itemPriceResponse);

    }

    @ExceptionHandler
    public ResponseEntity<ItemPriceResponse> HttpMessageNotReadableException(HttpMessageNotReadableException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_BAD_JSON_FORMAT);
        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("400");
        itemPriceResponse.setMetadata(customResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(itemPriceResponse);

    }
    @ExceptionHandler
    public ResponseEntity<ItemPriceResponse> BadRequestException(BadRequestException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_RESPONSE_BAD_REQUEST);
        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("400");
        itemPriceResponse.setMetadata(customResponse);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(itemPriceResponse);

    }

    @ExceptionHandler
    public ResponseEntity<ItemPriceResponse> IOException(IOException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_BAD_FILE_FORMAT);
        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("400");
        itemPriceResponse.setMetadata(customResponse);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(itemPriceResponse);

    }

    @ExceptionHandler
    public ResponseEntity<ItemPriceResponse> IllegalArgumentException(IllegalArgumentException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_BAD_DATA_CSV);
        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("400");
        itemPriceResponse.setMetadata(customResponse);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(itemPriceResponse);

    }

    @ExceptionHandler
    public ResponseEntity<ItemPriceResponse> ParseException(ParseException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_BAD_INCORRECT_DATA_FORMAT);
        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("400");
        itemPriceResponse.setMetadata(customResponse);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(itemPriceResponse);

    }

    @ExceptionHandler
    public ResponseEntity<ItemPriceResponse> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_METHOD_NOT_ALLOWED);
        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("405");
        itemPriceResponse.setMetadata(customResponse);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(itemPriceResponse);

    }

    @ExceptionHandler
    public ResponseEntity<ItemPriceResponse> NoResourceFoundException(NoResourceFoundException exc) {

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_METHOD_NOT_ALLOWED);
        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setStatus_code("404");
        itemPriceResponse.setMetadata(customResponse);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(itemPriceResponse);

    }





}
