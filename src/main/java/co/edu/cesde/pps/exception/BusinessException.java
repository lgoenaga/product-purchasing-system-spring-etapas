package co.edu.cesde.pps.exception;

/**
 * Excepción base para todos los errores de negocio del sistema.
 *
 * Esta clase sirve como padre de todas las excepciones específicas del dominio,
 * permitiendo un manejo centralizado de errores de negocio.
 *
 * Las subclases deben proporcionar mensajes descriptivos y contexto específico
 * del error que representan.
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructor con mensaje de error
     *
     * @param message Descripción del error de negocio
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa raíz
     *
     * @param message Descripción del error de negocio
     * @param cause Excepción original que causó este error
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor solo con causa raíz
     *
     * @param cause Excepción original que causó este error
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }
}
