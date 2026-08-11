package com.arquisoft.seguridad.domain.auth.secondaryport;

public interface TokenInvalidadoOutputPort {

    void invalidarToken(String jti, long ttlSegundos);

    boolean estaInvalidado(String jti);
}
