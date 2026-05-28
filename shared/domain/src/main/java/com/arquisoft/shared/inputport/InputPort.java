package com.arquisoft.shared.inputport;

/**
 * Contrato base para puertos de entrada (casos de uso) que retornan un resultado.
 *
 * <p>Toda interfaz de caso de uso que devuelva un valor debe extender este puerto.
 * La firma única {@code O ejecutar(I input)} estandariza el punto de entrada
 * de cada caso de uso en todos los contextos del sistema.
 *
 * <p>Uso:
 * <pre>{@code
 * public interface RegistrarFichaPerfilInputPort
 *         extends InputPort<RegistrarFichaPerfilCommand, UUID> {}
 * }</pre>
 *
 * @param <I> tipo del comando o criterio de entrada (input)
 * @param <O> tipo del resultado de salida (output)
 */
public interface InputPort<I, O> {

    /**
     * Ejecuta el caso de uso con el input dado y retorna el resultado.
     *
     * @param input comando o criterio que dispara el caso de uso
     * @return resultado producido por el caso de uso
     */
    O ejecutar(I input);
}
