package com.arquisoft.seguridad.domain.auth.port.out;

public interface TokenInvalidadoOutputPort {

    void invalidarToken(String jti, long ttlSegundos);

    boolean estaInvalidado(String jti);
}
