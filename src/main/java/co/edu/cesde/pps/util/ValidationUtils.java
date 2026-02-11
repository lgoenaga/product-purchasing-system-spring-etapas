package co.edu.cesde.pps.util;

import co.edu.cesde.pps.exception.ValidationException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Clase utilitaria para validaciones de datos del dominio.
 *
 * Proporciona métodos estáticos para validar:
 * - Valores nulos y vacíos
 * - Rangos numéricos
 * - Formatos de texto (email, teléfono)
 * - Cantidades y montos monetarios
 *
 * Lanza ValidationException con mensajes descriptivos cuando falla una validación.
 */
public final class ValidationUtils {

    // Patrón para validación de email (simplificado)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Patrón para validación de teléfono (formato flexible)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{7,15}$"
    );

    // Constructor privado para prevenir instanciación
    private ValidationUtils() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Valida que un objeto no sea nulo
     *
     * @param value Valor a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el valor es nulo
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, null, "Field cannot be null");
        }
    }

    /**
     * Valida que un String no sea nulo ni vacío
     *
     * @param value Valor a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el valor es nulo o vacío
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, value, "Field cannot be null or empty");
        }
    }

    /**
     * Valida que una colección no sea nula ni vacía
     *
     * @param collection Colección a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si la colección es nula o vacía
     */
    public static void validateNotEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(fieldName, collection, "Collection cannot be null or empty");
        }
    }

    /**
     * Valida que un valor BigDecimal sea positivo (mayor a cero)
     *
     * @param value Valor a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el valor es nulo, negativo o cero
     */
    public static void validatePositive(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName, value, "Value must be positive (> 0)");
        }
    }

    /**
     * Valida que un valor BigDecimal no sea negativo (mayor o igual a cero)
     *
     * @param value Valor a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el valor es nulo o negativo
     */
    public static void validateNonNegative(BigDecimal value, String fieldName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(fieldName, value, "Value cannot be negative");
        }
    }

    /**
     * Valida que un valor Integer sea positivo (mayor a cero)
     *
     * @param value Valor a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el valor es nulo, negativo o cero
     */
    public static void validatePositive(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new ValidationException(fieldName, value, "Value must be positive (> 0)");
        }
    }

    /**
     * Valida que un valor Integer no sea negativo (mayor o igual a cero)
     *
     * @param value Valor a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el valor es nulo o negativo
     */
    public static void validateNonNegative(Integer value, String fieldName) {
        if (value == null || value < 0) {
            throw new ValidationException(fieldName, value, "Value cannot be negative");
        }
    }

    /**
     * Valida que un valor esté dentro de un rango específico
     *
     * @param value Valor a validar
     * @param min Valor mínimo permitido (inclusivo)
     * @param max Valor máximo permitido (inclusivo)
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el valor está fuera del rango
     */
    public static void validateRange(Integer value, int min, int max, String fieldName) {
        validateNotNull(value, fieldName);
        if (value < min || value > max) {
            throw new ValidationException(fieldName, value,
                String.format("Value must be between %d and %d", min, max));
        }
    }

    /**
     * Valida que un String tenga una longitud mínima
     *
     * @param value Valor a validar
     * @param minLength Longitud mínima requerida
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el valor es más corto que la longitud mínima
     */
    public static void validateMinLength(String value, int minLength, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.length() < minLength) {
            throw new ValidationException(fieldName, value,
                String.format("Value must be at least %d characters long", minLength));
        }
    }

    /**
     * Valida que un String no exceda una longitud máxima
     *
     * @param value Valor a validar
     * @param maxLength Longitud máxima permitida
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el valor es más largo que la longitud máxima
     */
    public static void validateMaxLength(String value, int maxLength, String fieldName) {
        validateNotNull(value, fieldName);
        if (value.length() > maxLength) {
            throw new ValidationException(fieldName, value,
                String.format("Value must not exceed %d characters", maxLength));
        }
    }

    /**
     * Valida el formato de un email
     *
     * @param email Email a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el formato del email es inválido
     */
    public static void validateEmail(String email, String fieldName) {
        validateNotBlank(email, fieldName);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(fieldName, email, "Invalid email format");
        }
    }

    /**
     * Valida el formato de un teléfono
     *
     * @param phone Teléfono a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el formato del teléfono es inválido
     */
    public static void validatePhone(String phone, String fieldName) {
        if (phone != null && !phone.trim().isEmpty()) {
            String cleanPhone = phone.replaceAll("[\\s()-]", "");
            if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
                throw new ValidationException(fieldName, phone, "Invalid phone format");
            }
        }
    }

    /**
     * Valida que un SKU tenga formato válido
     *
     * @param sku SKU a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el SKU es inválido
     */
    public static void validateSku(String sku, String fieldName) {
        validateNotBlank(sku, fieldName);
        validateMinLength(sku, 3, fieldName);
        validateMaxLength(sku, 50, fieldName);
    }

    /**
     * Valida que un slug tenga formato válido (URL-friendly)
     *
     * @param slug Slug a validar
     * @param fieldName Nombre del campo para el mensaje de error
     * @throws ValidationException si el slug es inválido
     */
    public static void validateSlug(String slug, String fieldName) {
        validateNotBlank(slug, fieldName);
        if (!slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
            throw new ValidationException(fieldName, slug,
                "Slug must contain only lowercase letters, numbers and hyphens");
        }
    }
}
