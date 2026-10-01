package com.arquisoft.seguridad.application.auth.command.secondaryport;

public interface TokenInvalidadoOutputPort {

    void invalidarToken(String jti, long ttlSegundos);

    boolean estaInvalidado(String jti);
}
