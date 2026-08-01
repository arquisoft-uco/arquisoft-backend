package com.arquisoft.shared.usecase;

/**
 * Contrato base para casos de uso que no retornan resultado.
 *
 * <p>Toda interfaz de caso de uso que no devuelva valor debe extender este puerto.
 * La firma única {@code void ejecutar(I input)} estandariza el punto de entrada
 * de cada caso de uso de escritura puro en todos los contextos del sistema.
 *
 * <p>Uso:
 * <pre>{@code
 * public interface CambiarAsesorFichaUseCase
 *         extends VoidUseCase<CambiarAsesorFichaCommand> {}
 * }</pre>
 *
 * @param <I> tipo del comando de entrada (input)
 */
public interface VoidUseCase<I> {

    /**
     * Ejecuta el caso de uso con el input dado sin producir resultado.
     *
     * @param input comando que dispara el caso de uso
     */
    void ejecutar(I input);
}
