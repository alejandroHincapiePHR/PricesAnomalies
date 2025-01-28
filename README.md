# Price Anomaly Detection

## Descripción

**Price Anomaly Detection** es una aplicación diseñada para detectar anomalías en los precios de productos mediante algoritmos estadísticos. Utiliza MongoDB para almacenar los datos de productos y Redis para manejar la caché. La aplicación está construida con Spring Boot y se puede desplegar fácilmente utilizando Docker.

## Requisitos previos

- Docker y Docker Compose instalados en tu máquina.

## Despliegue

El despliegue de la aplicación puede realizarse utilizando Docker Compose. Para ello, se debe utilizar el archivo `docker-compose.yml` que configura los servicios necesarios para MongoDB, Redis y la propia aplicación.

### Dependencias

La aplicación depende de los siguientes servicios que se levantan automáticamente con Docker Compose:

- **MongoDB**: Base de datos NoSQL utilizada para almacenar los datos de productos.
- **Redis**: Sistema de almacenamiento en caché utilizado para optimizar el rendimiento de la aplicación.
- **Aplicación (App)**: La propia aplicación de detección de anomalías que se conecta a MongoDB y Redis.

### Variables de entorno

La aplicación utiliza varias variables de entorno configurables para adaptar su comportamiento según el entorno de ejecución. A continuación se detallan las variables principales que se pueden configurar:

- `SPRING_APPLICATION_NAME`: Nombre de la aplicación (por defecto: `PriceAnomalyDetection`).
- `SPRING_DATA_MONGODB_URI`: URI de conexión a la base de datos MongoDB (por defecto: `mongodb://localhost:27017/products`).
- `SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE`: Tamaño máximo de archivo para las solicitudes multipart (por defecto: `10MB`).
- `SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE`: Tamaño máximo de solicitud para los archivos multipart (por defecto: `10MB`).
- `SPRING_REDIS_HOST`: Dirección del servidor Redis (por defecto: `localhost`).
- `SPRING_REDIS_PORT`: Puerto del servidor Redis (por defecto: `6379`).
- `LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_CACHE`: Nivel de registro para la caché de Spring (por defecto: `TRACE`).
- `ALGORITHM_WINDOW_SIZE`: Tamaño de la ventana para el algoritmo de detección de anomalías (por defecto: `5`).
- `ALGORITHM_K_VALUE`: Valor `k` utilizado para calcular los límites en la detección de anomalías (por defecto: `1.5`).

### Configuración del Docker Compose

El archivo `docker-compose.yml` configurará y levantará los siguientes servicios:

1. **MongoDB**: Utiliza la imagen oficial de MongoDB (`mongo:6.0`), configurando las credenciales de acceso mediante las variables de entorno `MONGO_INITDB_ROOT_USERNAME` y `MONGO_INITDB_ROOT_PASSWORD`.
   
2. **Redis**: Utiliza la imagen oficial de Redis (`redis:7.0`), configurado en el puerto `6379`.

3. **Aplicación**: La aplicación se ejecuta en un contenedor separado y se conecta a MongoDB y Redis a través de las variables de entorno definidas en el archivo Docker Compose, incluyendo la configuración para la detección de anomalías (`ALGORITHM_WINDOW_SIZE` y `ALGORITHM_K_VALUE`).
  

Una vez configurado el archivo `docker-compose.yml` con las variables correspondientes, puedes iniciar todos los servicios con el siguiente comando:

```bash
docker-compose up -d
```

Esto levantará los contenedores necesarios para MongoDB, Redis y la aplicación. La aplicación estará disponible en el puerto `8080` de tu máquina local.

## Acceso a la aplicación

Una vez que los contenedores estén en funcionamiento, puedes acceder a la aplicación en el siguiente enlace:

[http://localhost:8080/api/v1/price-anomaly](http://localhost:8080/api/v1/price-anomaly)


# API de Detección de Anomalías en Precios

### Características Principales:
- **Detectar anomalías en los precios**: Verifica si un precio para un producto está fuera del rango esperado según los datos históricos.
- **Cargar datos de precios**: Permite cargar un archivo CSV con datos de precios de productos para su análisis y almacenamiento.
- **Fácil integración**: Endpoints RESTful diseñados para una integración sencilla en otras aplicaciones.

### Endpoints Disponibles:

1. **POST /isAnomaly**  
   Este endpoint verifica si el precio de un producto se considera una anomalía.
   - **Request**: Un payload JSON con el ID del artículo y el precio.
   - **Response**: El estado de la anomalía ("true" o "false"), el ID del artículo, el precio y los metadatos sobre la operación.
   
2. **POST /upload**  
   Este endpoint permite cargar un archivo CSV con los datos de precios de productos. El archivo debe contener las siguientes columnas: `ITEM_ID`, `PRICE` y `ORD_CLOSED_DT`.
   - **Request**: Un archivo CSV cargado como formulario con el campo "file".
   - **Response**: Un mensaje de éxito y el código de estado que indica el éxito de la carga del archivo.

3. **GET /{id}**  
   Este endpoint recupera los detalles de un producto por su ID.
   - **Request**: El ID del producto que se va a recuperar.
   - **Response**: Los detalles del producto en formato JSON o un error 404 si el producto no se encuentra.

### Manejo de Errores:
- **400 Bad Request**: Entrada inválida o parámetros faltantes.
- **404 Not Found**: Producto no encontrado con el ID especificado o recurso.
- **500 Internal Server Error**: Errores inesperados en el servidor o durante el procesamiento.

### Ejemplo de Solicitud y Respuesta:

#### 1. POST /isAnomaly
##### Solicitud:
```json
{
  "item_id": "12345",
  "price": 100.0
}
```

##### Respuesta:
```json
{
  "item_id": "12345",
  "price": 100.0,
  "anomaly": "true",
  "metadata": {
    "message": "Operación exitosa"
  },
  "status_code": "200"
}
```

#### 2. POST /upload
##### Solicitud:
Un archivo CSV con el siguiente formato:
```
ITEM_ID,PRICE,ORD_CLOSED_DT
12345,100.0,2023-01-01
12346,150.0,2023-01-02
```
el archivo se debe adjuntar con key =  "file" en el cuerpo de la peticion post 

##### Respuesta:
```json
{
  "metadata": {
    "message": "Datos cargados con éxito"
  },
  "status_code": "200"
}
```

### CI/CD 
GitHub Action pipeline integrado para construir y despliegar imagen de Docker a docker Hub cada vez que se hace commit sobre master
 **Imagen**:  [alh7867/price-anomaly-detection-meli](http://registry.hub.docker.com/r/alh7867/price-anomaly-detection-meli)

![image](https://github.com/user-attachments/assets/446920c4-2aba-4980-a27f-4708cde013ec)



### Arquitecturas planteadas ASINCRONA:

Caso de negocio: La aplicación de actualización de productos no requiere obtener respuesta en tiempo real de la anomalia en el precio (idealmente puesto que kafka agrega latencia) y estaria pensado en solo procesar informacion historica y enviar notificaciones o revertir precios posteriormente. 

![Solucion asincrona MELI](https://github.com/user-attachments/assets/04c710d0-6e81-4dab-9fca-0049ef1d1526)


### Arquitecturas planteadas SINCRONA:
Caso de negocio: proceso principal requiere saber si el precio es anomalo o no para aprobar el cambio, gracias al balanceador de carga y a las replicas de los PODs de K8 podemos manejar buenos tiepos de respuesta en las peticiones. 
![Solucion sincrona](https://github.com/user-attachments/assets/9ed0198c-6c73-4fcb-b60c-a2bbfdd152a9)

