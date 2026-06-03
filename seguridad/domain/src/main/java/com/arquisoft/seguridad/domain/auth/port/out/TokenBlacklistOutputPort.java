package com.arquisoft.seguridad.domain.auth.port.out;

/**
 * Puerto de salida para la blacklist de tokens JWT revocados explicitamente.
 *
 * Estrategia anti-saturacion: el TTL de cada entrada = vida restante del token.
 * Redis expira automaticamente la entrada cuando el token habria caducado de todas formas.
 * El tamano maximo del blacklist ≈ sesiones activas con logout pendiente, no el total historico.
 */
public interface TokenBlacklistOutputPort {

    /**
     * Registra el JTI del token como revocado durante {@code ttlSegundos}.
     * El TTL debe ser igual a la vida restante del token para que Redis
     * lo expire automaticamente sin acumulacion innecesaria.
     *
     * @param jti         claim "jti" del JWT (identificador unico)
     * @param ttlSegundos segundos restantes de vida del token
     */
    void invalidarToken(String jti, long ttlSegundos);

    /**
     * Verifica si el token fue revocado explicitamente.
     *
     * @param jti claim "jti" del JWT
     * @return true si el token esta en la blacklist
     */
    boolean estaInvalidado(String jti);
}
