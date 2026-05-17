package com.arquisoft.seguridad.application.auth.command;

public interface LogoutUseCase {

    /**
     * Invalida el token JWT representado por el comando.
     *
     * @param command comando con el JTI del token y los segundos restantes de vida
     */
    void ejecutar(LogoutCommand command);
}
