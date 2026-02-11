# Configuración del Ambiente - Etapa 06

## 📋 Requisitos Previos

- **Java 17** o superior
- **Maven 3.8+**
- **MySQL 8.0+**
- IDE (IntelliJ IDEA recomendado)

---

## 🔧 Configuración Paso a Paso

### 1. Instalar y Configurar MySQL

#### En Linux/Mac:
```bash
# Instalar MySQL
sudo apt install mysql-server  # Ubuntu/Debian
brew install mysql              # macOS

# Iniciar MySQL
sudo systemctl start mysql      # Linux
mysql.server start              # macOS

# Acceder a MySQL
mysql -u root -p
```

#### En Windows:
- Descargar MySQL Installer desde: https://dev.mysql.com/downloads/installer/
- Seguir asistente de instalación
- Configurar password para root

### 2. Crear Base de Datos

```sql
CREATE DATABASE pps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

O ejecutar el script completo:
```bash
mysql -u root -p < src/main/resources/sql/schema.sql
mysql -u root -p pps_db < src/main/resources/sql/data.sql
```

### 3. Configurar Variables de Entorno

Copiar el archivo de ejemplo y ajustar valores:
```bash
cp .env.example .env
```

Editar `.env` con tus credenciales:
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=pps_db
DB_USER=root
DB_PASSWORD=tu_password_aqui
DB_DDL_AUTO=update
DB_SHOW_SQL=true
APP_ENVIRONMENT=development
```

**IMPORTANTE**: El archivo `.env` está en `.gitignore` y NO debe subirse a Git.

### 4. Cargar Variables de Entorno

Las variables de entorno se pueden cargar de varias formas:

#### Opción A: En el IDE (IntelliJ IDEA)
1. Run → Edit Configurations
2. En "Environment variables" agregar:
   ```
   DB_HOST=localhost;DB_PORT=3306;DB_NAME=pps_db;DB_USER=root;DB_PASSWORD=tu_password
   ```

#### Opción B: Desde terminal (Linux/Mac)
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=pps_db
export DB_USER=root
export DB_PASSWORD=tu_password
```

#### Opción C: Script de inicio (recomendado)
Crear archivo `run.sh`:
```bash
#!/bin/bash
source .env
export $(cat .env | xargs)
mvn clean compile exec:java
```

### 5. Descargar Dependencias Maven

```bash
mvn clean install
```

Esto descargará:
- Hibernate Core 6.4.4
- MySQL Connector 8.3.0
- SLF4J + Logback

### 6. Verificar Configuración

Compilar proyecto:
```bash
mvn clean compile
```

Si todo está correcto, verás:
```
[INFO] BUILD SUCCESS
```

---

## 📁 Estructura de Archivos de Configuración

```
src/main/resources/
├── META-INF/
│   └── persistence.xml          # Configuración JPA/Hibernate
├── logback.xml                  # Configuración de logging
└── sql/
    ├── schema.sql              # DDL - Estructura de BD
    └── data.sql                # DML - Datos iniciales
```

---

## 🔍 Verificar Conexión a MySQL

Desde MySQL CLI:
```sql
USE pps_db;
SHOW TABLES;
```

Deberías ver las tablas creadas por Hibernate o el script SQL.

---

## 🐛 Troubleshooting

### Error: "Access denied for user"
```
Causa: Credenciales incorrectas
Solución: Verificar DB_USER y DB_PASSWORD en .env
```

### Error: "Unknown database 'pps_db'"
```
Causa: Base de datos no creada
Solución: Ejecutar: CREATE DATABASE pps_db;
```

### Error: "Cannot resolve symbol 'JpaRepository'"
```
Causa: Dependencias no descargadas
Solución: mvn clean install
```

### Error: "Communications link failure"
```
Causa: MySQL no está corriendo
Solución: sudo systemctl start mysql (Linux) o mysql.server start (Mac)
```

### Error en persistence.xml con variables
```
Causa: Hibernate no resuelve ${...} automáticamente
Solución: Las variables se configurarán programáticamente en DatabaseConfig
```

---

## 📊 Logging

Los logs se guardan en:
```
logs/
├── application.log         # Log general
├── error.log              # Solo errores
└── sql.log                # Queries SQL
```

Niveles de logging configurables en `logback.xml`:
- **TRACE**: Muy detallado (bind parameters)
- **DEBUG**: Información de depuración (SQL queries)
- **INFO**: Información general
- **WARN**: Advertencias
- **ERROR**: Errores

---

## ⚙️ Configuración por Ambiente

### Desarrollo (development)
```properties
DB_DDL_AUTO=update
DB_SHOW_SQL=true
LOG_LEVEL=DEBUG
```

### Producción (production)
```properties
DB_DDL_AUTO=none
DB_SHOW_SQL=false
LOG_LEVEL=WARN
```

---

## 🚀 Próximos Pasos (Etapa 07)

Una vez verificada la configuración:
1. ✅ Agregar anotaciones `@Entity` a los modelos
2. ✅ Crear interfaces `Repository`
3. ✅ Actualizar `Services` para usar repositorios
4. ✅ Agregar `@Transactional`
5. ✅ Probar persistencia real

---

## 📝 Notas Importantes

1. **persistence.xml**: No usar Spring, solo JPA estándar
2. **.env**: NO subir a Git (ya está en .gitignore)
3. **Variables de entorno**: Se cargan en DatabaseConfig
4. **DDL Auto**: Usar `update` solo en desarrollo
5. **Logging**: Ajustar niveles según necesidad

---

## ✅ Checklist de Verificación

- [ ] MySQL instalado y corriendo
- [ ] Base de datos `pps_db` creada
- [ ] Archivo `.env` configurado
- [ ] Variables de entorno cargadas
- [ ] Dependencias Maven descargadas
- [ ] Proyecto compila sin errores
- [ ] Conexión a BD verificada

---

**Si todos los checks están ✅, estás listo para la Etapa 07!**
