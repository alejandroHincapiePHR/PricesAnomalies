package Product.PriceAnomalyDetection.controller;

import Product.PriceAnomalyDetection.commons.CustomResponse;
import Product.PriceAnomalyDetection.constant.MessageConstants;
import Product.PriceAnomalyDetection.errorHandling.exceptions.ProductNotFoundException;
import Product.PriceAnomalyDetection.model.ItemPriceRequest;
import Product.PriceAnomalyDetection.model.ItemPriceResponse;
import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.service.IProductService;
import Product.PriceAnomalyDetection.service.IUploadDataService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

import static Product.PriceAnomalyDetection.constant.ApiGlobalConstant.*;

@RestController
@RequestMapping(API_PRODUCT)
public class ProductController {

    private final IProductService productService;
    private final IUploadDataService uploadDataService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(IProductService productService, IUploadDataService uploadDataService) {
        this.productService = productService;
        this.uploadDataService = uploadDataService;
    }


    /**
     * Checks if a given price for a specific item is considered an anomaly.

     * This endpoint accepts a JSON request containing an item ID and a price. It uses the
     * {@link Product.PriceAnomalyDetection.service.ProductService} to determine if the provided price is an anomaly compared to
     * historical data for the item. The response includes the item ID, the provided price,
     * the anomaly status, metadata, and custom headers.
     *
     * @param request The {@link ItemPriceRequest} object containing the item ID and price
     *                to be checked. The request body must be a valid JSON object with the
     *                following structure:
     *                <pre>
     *                {
     *                    "item_id": "12345",
     *                    "price": 100.0
     *                }
     *                </pre>
     * @return A {@link ResponseEntity} containing an {@link ItemPriceResponse} object.
     *         The response includes:
     *         <ul>
     *             <li><b>item_id:</b> The ID of the item being checked.</li>
     *             <li><b>price:</b> The price provided in the request.</li>
     *             <li><b>anomaly:</b> A string indicating whether the price is an anomaly
     *                 (e.g., "Yes", "No").</li>
     *             <li><b>metadata:</b> A {@link CustomResponse} object with a success message.</li>
     *             <li><b>status_code:</b> The HTTP status code (200 for success).</li>
     *             <li><b>Headers:</b> Custom headers including API method, version, and content type.</li>
     *         </ul>
     * @throws ProductNotFoundException If the item ID provided in the request does not exist
     *                                  in the system.
     *
     * @see Product.PriceAnomalyDetection.service.ProductService
     * @see ItemPriceRequest
     * @see ItemPriceResponse
     * @see CustomResponse
     * @see HttpHeaders
     * @see ResponseEntity
     *
     * @example Example request:
     * <pre>
     * POST /isAnomaly
     * Content-Type: application/json
     *
     * {
     *     "item_id": "12345",
     *     "price": 100.0
     * }
     * </pre>
     *
     * @example Example response:
     * <pre>
     * {
     *     "item_id": "12345",
     *     "price": 100.0,
     *     "anomaly": "No",
     *     "metadata": {
     *         "message": "Operation successful"
     *     },
     *     "status_code": "200"
     * }
     * </pre>
     *
     * @apiNote Custom headers included in the response:
     * <ul>
     *     <li><b>API-Method:</b> Indicates the API method used (e.g., "isAnomaly").</li>
     *     <li><b>API-Version:</b> Indicates the API version (e.g., "v1").</li>
     *     <li><b>Content-Type:</b> Specifies the response content type (e.g., "application/json").</li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<ItemPriceResponse> isAnomaly(@Valid @RequestBody ItemPriceRequest request) throws ProductNotFoundException {

        String itemId = request.getItem_id();
        BigDecimal newPrice = request.getPrice();

        String isAnomaly = productService.isAnomaly(itemId, newPrice);

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(MessageConstants.MSG_RESPONSE_OK);

        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setItem_id(itemId);
        itemPriceResponse.setPrice(newPrice);
        itemPriceResponse.setAnomaly(isAnomaly);
        itemPriceResponse.setMetadata(customResponse);
        itemPriceResponse.setStatus_code(String.valueOf(HttpStatus.OK.value()));


        HttpHeaders headers = new HttpHeaders();
        headers.add("API-Method", API_METHOD_IS_ANOMALY);
        headers.add("API-Version", API_VERSION);
        headers.add("Content-Type", API_CONTENT_TYPE);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(itemPriceResponse);
    }

    /**
     * Handles the upload of a CSV file containing item price data.
     *
     * This endpoint accepts a CSV file as a multipart form-data request, processes the file
     * using the {@link IUploadDataService}, and returns a response indicating the success
     * of the operation. The response includes metadata, a status code, and custom headers.
     *
     * @param file The CSV file to be uploaded. The file must contain the following columns:
     *             ITEM_ID, PRICE, and ORD_CLOSED_DT. The file should be provided as a
     *             multipart form-data parameter with the key "file".
     * @return A {@link ResponseEntity} containing an {@link ItemPriceResponse} object.
     *         The response includes:
     *         <ul>
     *             <li><b>Metadata:</b> A {@link CustomResponse} object with a success message.</li>
     *             <li><b>Status Code:</b> The HTTP status code (200 for success).</li>
     *             <li><b>Headers:</b> Custom headers including API method, version, and content type.</li>
     *         </ul>
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws ParseException If the file contains invalid data (e.g., incorrect date format).
     *
     * @see IUploadDataService
     * @see ItemPriceResponse
     * @see CustomResponse
     * @see HttpHeaders
     * @see ResponseEntity
     *
     * @example Example request:
     * <pre>
     * POST /upload
     * Content-Type: multipart/form-data
     *
     * file: example.csv
     * </pre>
     *
     * @example Example response:
     * <pre>
     * {
     *     "metadata": {
     *         "message": "Data cargada con exito"
     *     },
     *     "status_code": "200"
     * }
     * </pre>
     *
     * @apiNote The CSV file must adhere to the following format:
     * <pre>
     * ITEM_ID,PRICE,ORD_CLOSED_DT
     * 1,10.5,2023-10-01
     * 2,20.0,2023-10-02
     * </pre>
     *
     * @apiNote Custom headers included in the response:
     * <ul>
     *     <li><b>API-Method:</b> Indicates the API method used (e.g., "upload").</li>
     *     <li><b>API-Version:</b> Indicates the API version (e.g., "v1").</li>
     *     <li><b>Content-Type:</b> Specifies the response content type (e.g., "application/json").</li>
     * </ul>
     */
    @PostMapping("/upload")
    public ResponseEntity<ItemPriceResponse> uploadData(@RequestParam("file") MultipartFile file) throws IOException, ParseException {

        uploadDataService.processFile(file);

        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage("Data cargada con exito");

        ItemPriceResponse itemPriceResponse = new ItemPriceResponse();
        itemPriceResponse.setMetadata(customResponse);
        itemPriceResponse.setStatus_code(String.valueOf(HttpStatus.OK.value()));


        HttpHeaders headers = new HttpHeaders();
        headers.add("API-Method", API_METHOD_UPLOAD);
        headers.add("API-Version", API_VERSION);
        headers.add("Content-Type", API_CONTENT_TYPE);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(itemPriceResponse);


    }


    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") String id) {

        Product product = productService.findById(id);

        if(product == null){
            throw new ProductNotFoundException();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(product);
    }


}
