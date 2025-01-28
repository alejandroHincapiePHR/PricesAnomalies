package Product.PriceAnomalyDetection.controller;

import Product.PriceAnomalyDetection.controller.errorHandling.ControllerAdvice;
import Product.PriceAnomalyDetection.controller.errorHandling.exceptions.ProductNotFoundException;
import Product.PriceAnomalyDetection.model.ItemPriceRequest;
import Product.PriceAnomalyDetection.model.PriceData;
import Product.PriceAnomalyDetection.model.Product;
import Product.PriceAnomalyDetection.service.productService.IProductService;
import Product.PriceAnomalyDetection.service.uploadService.UploadDataServiceImp;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static Product.PriceAnomalyDetection.controller.constant.ApiGlobalConstant.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IProductService productService;

    @Mock
    private UploadDataServiceImp uploadDataService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new ControllerAdvice())
                .build();
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testIsAnomaly_ReturnsTrue() throws Exception {

        String itemId = "MLB3519541231";
        BigDecimal price = new BigDecimal("789");
        ItemPriceRequest request = new ItemPriceRequest(itemId, price);

        when(productService.isAnomaly(any(String.class), any(BigDecimal.class))).thenReturn("true");

        mockMvc.perform(post("/" + API_PRODUCT )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.item_id").value(itemId))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.anomaly").value("true"))
                .andExpect(jsonPath("$.status_code").value("200"));
    }

    @Test
    public void testIsAnomaly_ReturnsFalse() throws Exception {

        String itemId = "MLB3519541231";
        BigDecimal price = new BigDecimal("789");
        ItemPriceRequest request = new ItemPriceRequest(itemId, price);

        when(productService.isAnomaly(any(String.class), any(BigDecimal.class))).thenReturn("false");


        mockMvc.perform(post("/" + API_PRODUCT )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.item_id").value(itemId))
                .andExpect(jsonPath("$.price").value(price))
                .andExpect(jsonPath("$.anomaly").value("false"))
                .andExpect(jsonPath("$.status_code").value("200"));
    }


    @Test
    public void testIsAnomaly_ProductNotFoundException() throws Exception {

        String itemId = "MLB3519541231";
        BigDecimal price = new BigDecimal("789");
        ItemPriceRequest request = new ItemPriceRequest(itemId, price);

        when(productService.isAnomaly(any(String.class), any(BigDecimal.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));


        mockMvc.perform(post("/" + API_PRODUCT )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindById_WithNullOutliersAndNonOutliers() throws Exception {
        Product product = new Product();
        String id = "test";
        product.setId(id);
        when(productService.findById(id)).thenReturn(product);


        mockMvc.perform(get("/" + API_PRODUCT + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.outliers").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.nonOutliers").value(IsNull.nullValue()));
    }

    @Test
    public void testFindById_WithEmptyOutliersAndNonOutliers() throws Exception {

        Product product = new Product();
        String id = "test";
        product.setId(id);
        product.setOutliers(new ArrayList<>());
        product.setNonOutliers(new ArrayList<>());
        when(productService.findById(id)).thenReturn(product);


        mockMvc.perform(get("/" + API_PRODUCT + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.outliers").isArray())
                .andExpect(jsonPath("$.nonOutliers").isArray())
                .andExpect(jsonPath("$.outliers").isEmpty())
                .andExpect(jsonPath("$.nonOutliers").isEmpty());
    }

    @Test
    public void testFindById_WithNonEmptyOutliersAndNonOutliers() throws Exception {

        Product product = new Product();
        String id = "test";
        product.setId(id);

        ArrayList<PriceData> outliers = new ArrayList<>();
        ArrayList<PriceData> nonOutliers = new ArrayList<>();

        Date date = new Date();
        PriceData priceData = new PriceData(date, BigDecimal.valueOf(0));
        outliers.add(priceData);
        nonOutliers.add(priceData);

        product.setOutliers(outliers);
        product.setNonOutliers(nonOutliers);

        when(productService.findById(id)).thenReturn(product);

        mockMvc.perform(get("/" + API_PRODUCT + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.outliers").isArray())
                .andExpect(jsonPath("$.nonOutliers").isArray())
                .andExpect(jsonPath("$.outliers").isNotEmpty())
                .andExpect(jsonPath("$.nonOutliers").isNotEmpty());
    }

    @Test
    public void testFindById_nullProduct() throws Exception {
        String id = "test";
        when(productService.findById(id)).thenThrow(new ProductNotFoundException());

        mockMvc.perform(get("/" + API_PRODUCT + "/" + id))
                .andExpect(status().isNotFound());

    }


}
