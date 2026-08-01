package com.arquisoft.seguridad.domain.auth.port.out;

public interface TokenBlacklistOutputPort {

    void invalidarToken(String jti, long ttlSegundos);

    boolean estaInvalidado(String jti);
}
