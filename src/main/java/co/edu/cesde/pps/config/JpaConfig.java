package co.edu.cesde.pps.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración JPA para uso de EntityManager sin Spring.
 *
 * Responsabilidades:
 * - Crear y mantener el EntityManagerFactory (singleton)
 * - Proveer EntityManager por operación
 * - Permitir cierre explícito en shutdown
 *
 * Nota:
 * - Usa el persistence-unit definido en META-INF/persistence.xml
 * - Sobrescribe propiedades JDBC con variables de entorno (DatabaseConfig)
 */
public final class JpaConfig {

    private static final String PERSISTENCE_UNIT_NAME = "pps-persistence-unit";

    private static volatile EntityManagerFactory entityManagerFactory;

    private JpaConfig() {
        throw new AssertionError("JpaConfig is a utility class and cannot be instantiated");
    }

    /**
     * Obtiene (o crea) el EntityManagerFactory.
     *
     * @return EntityManagerFactory singleton
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            synchronized (JpaConfig.class) {
                if (entityManagerFactory == null) {
                    entityManagerFactory = buildEntityManagerFactory();
                }
            }
        }
        return entityManagerFactory;
    }

    /**
     * Crea un EntityManager nuevo.
     *
     * @return EntityManager
     */
    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    /**
     * Cierra el EntityManagerFactory si está abierto.
     *
     * Importante: llamar al finalizar la aplicación (shutdown hook).
     */
    public static void close() {
        synchronized (JpaConfig.class) {
            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
            entityManagerFactory = null;
        }
    }

    private static EntityManagerFactory buildEntityManagerFactory() {
        // Sobrescribir propiedades JDBC usando DatabaseConfig.
        // Esto asegura que aunque el persistence.xml tenga defaults, se usarán las variables de entorno.
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", DatabaseConfig.getJdbcUrl());
        props.put("jakarta.persistence.jdbc.user", DatabaseConfig.getDbUser());
        props.put("jakarta.persistence.jdbc.password", DatabaseConfig.getDbPassword());
        props.put("jakarta.persistence.jdbc.driver", DatabaseConfig.getDriverClassName());

        // Propiedades adicionales (opcionales) coherentes con DatabaseConfig.
        // No forzamos hbm2ddl aquí para no interferir con lo definido en persistence.xml,
        // pero dejamos la facilidad si en el futuro se decide controlar por env.
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, props);
    }
}
