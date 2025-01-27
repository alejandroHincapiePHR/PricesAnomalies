package Product.PriceAnomalyDetection.constant;

public class MessageConstants {

    public static final String MSG_RESPONSE_OK= "Exito al ejecutar la operación";

    public static final String MSG_RESPONSE_NOT_FOUND = "Recurso no encontrado";

    public static final String MSG_RESPONSE_BAD_REQUEST= "El tipo de dato proporcionado no es el adecuado" +
            " (null o de tipo incorrecto)";

    public static final String MSG_BAD_JSON_FORMAT= "Formato JSON incorrecto";
    public static final String MSG_BAD_FILE_FORMAT= "El tipo de archivo aceptado es CSV para cargar data";
    public static final String MSG_BAD_DATA_CSV= "Data en archivo CSV esta corrupta no se puede leer" +
            ", revise los requisitos del archivo";
    public static final String MSG_BAD_INCORRECT_DATA_FORMAT= "Data en el archivo CSV no esta en formato correcto" +
            "revise los requisitos del archivo";
    public static final String MSG_METHOD_NOT_ALLOWED= "Metodo HTTP no permitido, revise documentacion para conocer los metodos" +
            "HTTP permitidos.";



}
