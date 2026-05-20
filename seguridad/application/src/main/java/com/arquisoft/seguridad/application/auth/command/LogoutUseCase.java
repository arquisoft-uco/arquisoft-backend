package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.dto.LogoutRequestDTO;

public interface LogoutUseCase {

    /**
     * Invalida el token JWT representado por el DTO de solicitud.
     *
     * @param request DTO con el JTI del token y los segundos restantes de vida
     */
    void ejecutar(LogoutRequestDTO request);
}
