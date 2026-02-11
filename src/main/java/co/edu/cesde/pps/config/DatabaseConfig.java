package co.edu.cesde.pps.config;

/**
 * Configuración de base de datos.
 *
 * Centraliza todas las propiedades de conexión a la base de datos MySQL.
 * Utiliza variables de entorno para diferentes ambientes (dev, staging, prod).
 *
 * Esta clase se preparará para Hibernate/JPA en la Etapa 06.
 *
 * Variables de entorno esperadas:
 * - DB_HOST: Host del servidor MySQL (default: localhost)
 * - DB_PORT: Puerto de MySQL (default: 3306)
 * - DB_NAME: Nombre de la base de datos (default: pps_db)
 * - DB_USER: Usuario de MySQL (default: user_pps) - NO usar root en producción
 * - DB_PASSWORD: Contraseña de MySQL (default: vacío)
 * - DB_POOL_SIZE: Tamaño del pool de conexiones (default: 10)
 */
public class DatabaseConfig {

    // Propiedades de conexión por defecto (desarrollo)
    private static final String DEFAULT_DB_HOST = "localhost";
    private static final String DEFAULT_DB_PORT = "3306";
    private static final String DEFAULT_DB_NAME = "pps_db";
    private static final String DEFAULT_DB_USER = "user_pps"; // NO usar root
    private static final String DEFAULT_DB_PASSWORD = "";

    // Propiedades de Hibernate/JPA
    private static final String DEFAULT_DIALECT = "org.hibernate.dialect.MySQL8Dialect";
    private static final String DEFAULT_DDL_AUTO = "update"; // create, update, validate, none
    private static final boolean DEFAULT_SHOW_SQL = true;
    private static final boolean DEFAULT_FORMAT_SQL = true;
    private static final boolean DEFAULT_USE_SQL_COMMENTS = true;

    // Propiedades del pool de conexiones
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final int DEFAULT_MIN_POOL_SIZE = 5;
    private static final int DEFAULT_MAX_POOL_SIZE = 20;
    private static final int DEFAULT_IDLE_TIMEOUT = 300000; // 5 minutos en ms

    // Constructor privado para prevenir instanciación
    private DatabaseConfig() {
        throw new AssertionError("DatabaseConfig is a utility class and cannot be instantiated");
    }

    /**
     * Obtiene el host del servidor MySQL desde variables de entorno.
     *
     * @return host de MySQL
     */
    public static String getDbHost() {
        return System.getenv().getOrDefault("DB_HOST", DEFAULT_DB_HOST);
    }

    /**
     * Obtiene el puerto de MySQL desde variables de entorno.
     *
     * @return puerto de MySQL
     */
    public static String getDbPort() {
        return System.getenv().getOrDefault("DB_PORT", DEFAULT_DB_PORT);
    }

    /**
     * Obtiene el nombre de la base de datos desde variables de entorno.
     *
     * @return nombre de la base de datos
     */
    public static String getDbName() {
        return System.getenv().getOrDefault("DB_NAME", DEFAULT_DB_NAME);
    }

    /**
     * Obtiene el usuario de MySQL desde variables de entorno.
     *
     * @return usuario de MySQL
     */
    public static String getDbUser() {
        return System.getenv().getOrDefault("DB_USER", DEFAULT_DB_USER);
    }

    /**
     * Obtiene la contraseña de MySQL desde variables de entorno.
     *
     * @return contraseña de MySQL
     */
    public static String getDbPassword() {
        return System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_DB_PASSWORD);
    }

    /**
     * Construye la URL de conexión JDBC completa.
     *
     * Formato: jdbc:mysql://host:port/database?useSSL=false&serverTimezone=UTC
     *
     * @return URL de conexión JDBC
     */
    public static String getJdbcUrl() {
        return String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            getDbHost(),
            getDbPort(),
            getDbName()
        );
    }

    /**
     * Obtiene el dialecto de Hibernate para MySQL.
     *
     * @return dialecto de Hibernate
     */
    public static String getHibernateDialect() {
        return DEFAULT_DIALECT;
    }

    /**
     * Obtiene la estrategia de DDL auto de Hibernate.
     *
     * Valores posibles:
     * - create: Borra y recrea el esquema en cada inicio
     * - update: Actualiza el esquema (recomendado para desarrollo)
     * - validate: Solo valida el esquema
     * - none: No hace nada (recomendado para producción)
     *
     * @return estrategia DDL auto
     */
    public static String getHibernateDdlAuto() {
        String env = AppConfig.getEnvironment();
        if (AppConfig.isProduction()) {
            return "none"; // En producción no modificar esquema
        }
        return System.getenv().getOrDefault("DB_DDL_AUTO", DEFAULT_DDL_AUTO);
    }

    /**
     * Indica si se debe mostrar el SQL generado en logs.
     *
     * @return true si se debe mostrar SQL
     */
    public static boolean isShowSql() {
        if (AppConfig.isProduction()) {
            return false; // En producción no mostrar SQL
        }
        String showSql = System.getenv().getOrDefault("DB_SHOW_SQL", String.valueOf(DEFAULT_SHOW_SQL));
        return Boolean.parseBoolean(showSql);
    }

    /**
     * Indica si se debe formatear el SQL en logs.
     *
     * @return true si se debe formatear SQL
     */
    public static boolean isFormatSql() {
        return DEFAULT_FORMAT_SQL;
    }

    /**
     * Indica si se deben incluir comentarios en el SQL generado.
     *
     * @return true si se deben incluir comentarios
     */
    public static boolean isUseSqlComments() {
        return DEFAULT_USE_SQL_COMMENTS;
    }

    /**
     * Obtiene el tamaño del pool de conexiones.
     *
     * @return tamaño del pool
     */
    public static int getPoolSize() {
        String poolSize = System.getenv().getOrDefault("DB_POOL_SIZE", String.valueOf(DEFAULT_POOL_SIZE));
        return Integer.parseInt(poolSize);
    }

    /**
     * Obtiene el tamaño mínimo del pool de conexiones.
     *
     * @return tamaño mínimo del pool
     */
    public static int getMinPoolSize() {
        return DEFAULT_MIN_POOL_SIZE;
    }

    /**
     * Obtiene el tamaño máximo del pool de conexiones.
     *
     * @return tamaño máximo del pool
     */
    public static int getMaxPoolSize() {
        return DEFAULT_MAX_POOL_SIZE;
    }

    /**
     * Obtiene el timeout de inactividad para conexiones del pool (en ms).
     *
     * @return timeout de inactividad
     */
    public static int getIdleTimeout() {
        return DEFAULT_IDLE_TIMEOUT;
    }

    /**
     * Obtiene el driver JDBC de MySQL.
     *
     * @return nombre de clase del driver
     */
    public static String getDriverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    /**
     * Imprime la configuración actual de la base de datos (sin mostrar contraseña).
     * Útil para debug y verificación de configuración.
     *
     * @return String con información de configuración
     */
    public static String getConfigSummary() {
        return "DatabaseConfig{" +
                "host='" + getDbHost() + '\'' +
                ", port='" + getDbPort() + '\'' +
                ", database='" + getDbName() + '\'' +
                ", user='" + getDbUser() + '\'' +
                ", jdbcUrl='" + getJdbcUrl() + '\'' +
                ", dialect='" + getHibernateDialect() + '\'' +
                ", ddlAuto='" + getHibernateDdlAuto() + '\'' +
                ", showSql=" + isShowSql() +
                ", poolSize=" + getPoolSize() +
                '}';
    }
}
