# Sistema de Carga de Pedidos

## Instrucciones para ejecutar localmente

### Requisitos previos
- Java 17 o superior (microsoft/openjdk:17.0.10-jdk usado en este proyecto)
- Maven 3.9.11+
- PostgreSQL 16+ o superior
- Puerto 8095 disponible

### NOTAS IMPORTANTES
- Para una manera rapida de ejecutar el proyecto se pude usar: ` java -jar OrderLoader.jar `
- Se usó la tecnica de vertical slicing para la organizacion de paquetes y modulos del proyecto
- Los ejemplos de archivos CSV se encuentran en `src/main/resources/pedidos.csv y pedidos2.csv`
- El postman a importar está en `src/main/resources/OderLoader.postman_collection.json`
- El proyecto usa Flyway para migraciones de base de datos, los inserts y clientes estan en `src/main/resources/db/migration/`
### 1. Configurar la base de datos

Crear la base de datos PostgreSQL:
```sql
CREATE DATABASE dinet;
```

### 2. Configurar credenciales

El archivo `src/main/resources/application.yml` contiene la configuración por defecto:
- **URL**: `jdbc:postgresql://localhost:5432/dinet`
- **Usuario**: `postgres`
- **Contraseña**: `root`

Si tus credenciales son diferentes, modifica el archivo `application.yml`.

### 3. Compilar el proyecto

```bash
mvn clean install
```

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

O ejecutar el JAR generado:
```bash
java -jar target/csv-1.0-SNAPSHOT.jar
```

### 5. Acceder a la aplicación

- **API REST**: http://localhost:8095
- **Swagger UI**: http://localhost:8095/swagger-ui.html
- **API Docs**: http://localhost:8095/v3/api-docs

### 6. Migraciones de base de datos

Las migraciones Flyway se ejecutan automáticamente al iniciar la aplicación. Los scripts se encuentran en:
- `src/main/resources/db/migration/V1__crear_tablas.sql`
- `src/main/resources/db/migration/V2__datos_prueba.sql`

---

## Estrategia de Batch

### Descripción general

El sistema procesa archivos CSV con pedidos utilizando una estrategia de **procesamiento por streaming en chunks** para optimizar el rendimiento y minimizar el uso de memoria, permitiendo procesar archivos con millones de registros.

### Configuración

```yaml
batch:
  size: 500        # Tamaño de lote para inserción en BD
  chunk: 10000     # Hardcodeado tamaño del chunk para procesamiento en memoria
```

- **batch.size**: pedidos que se insertan juntos en la base de datos (por defecto: 500)
- **CHUNK_SIZE**: filas que se procesan simultáneamente en memoria (hardcodeado: 10,000)

### Estrategia batch (Streaming + Chunks)

1. Se itera por stream el archivo CSV
2. Se leeen por chunks(bloques) de 10k filas
3. Por cada chunk:
   - Se extrae IDs únicos (clientes, zonas, pedidos) y se validan en memoria
   - Guarda pedidos válidos en lotes de 500
4. Se tiene un Set global de números de pedido para detectar duplicados entre chunks

### Logs
Se usa sl4j para los logs