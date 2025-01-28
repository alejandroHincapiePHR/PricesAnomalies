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

[http://localhost:8080](http://localhost:8080)

## Contribuciones

Si deseas contribuir a este proyecto, por favor abre un _pull request_ o crea un _issue_ con tus sugerencias.

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.
