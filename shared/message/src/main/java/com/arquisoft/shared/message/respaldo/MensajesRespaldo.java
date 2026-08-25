package com.arquisoft.shared.message.respaldo;

import com.arquisoft.shared.message.CategoriaMensaje;
import com.arquisoft.shared.message.ClaveMensaje;

/**
 * Textos genéricos compilados, uno por categoría de mensaje.
 *
 * <p>Son el último recurso cuando el catálogo no puede resolver una clave. Con el arranque
 * fail-fast — la aplicación no levanta si Redis no responde o si al catálogo le falta una clave —
 * esta rama es inalcanzable tras un arranque exitoso; existe para que ninguna ruta de código
 * devuelva la clave cruda ni propague una excepción al cliente.
 */
public final class MensajesRespaldo {

    private MensajesRespaldo() {}

    /** Texto de servicio no disponible, el que ve el cliente si el catálogo cae en caliente. */
    public static final String ERROR = "El servicio no está disponible en este momento. Intente nuevamente más tarde.";

    /** Texto genérico de fallo de validación de datos de entrada. */
    public static final String VALIDACION = "Error de validación en los datos enviados";

    /** Marcador de documentación OpenAPI. No se resuelve en tiempo de ejecución. */
    public static final String API = "Documentación no disponible";

    /**
     * Devuelve el texto de respaldo que corresponde a la clave.
     *
     * <p>Las claves de log devuelven la clave misma: su audiencia es técnica y el nombre de la
     * clave dice más que un texto genérico.
     *
     * @param clave clave del catálogo
     * @return el texto de respaldo de su categoría
     */
    public static String para(ClaveMensaje clave) {
        return switch (CategoriaMensaje.desde(clave.clave())) {
            case VALIDACION -> VALIDACION;
            case API -> API;
            case LOG -> clave.clave();
            case ERROR -> ERROR;
        };
    }
}
