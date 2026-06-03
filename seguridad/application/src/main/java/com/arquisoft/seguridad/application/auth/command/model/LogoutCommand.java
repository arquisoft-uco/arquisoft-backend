package com.arquisoft.seguridad.application.auth.command.model;

import com.arquisoft.seguridad.application.util.message.SeguridadApplicationMessages;
import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.util.UtilObject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record LogoutCommand(
        @NotNull(message = "El JTI del token es requerido")
        String jti,

        @Positive(message = "Los segundos restantes deben ser mayores a cero")
        long ttlSegundos) {

    public LogoutCommand {
        if (UtilObject.isNull(jti)) {
            log.warn(SeguridadApplicationMessages.LogoutCommand.JTI_NULL_LOG);
            throw new ApplicationException(
                    SeguridadApplicationMessages.LogoutCommand.DATOS_SESION_INVALIDOS,
                    SeguridadApplicationMessages.LogoutCommand.CODIGO_SESION_INVALIDA);
        }
        if (ttlSegundos <= 0) {
            log.warn(SeguridadApplicationMessages.LogoutCommand.TTL_INVALIDO_LOG, ttlSegundos);
            throw new ApplicationException(
                    SeguridadApplicationMessages.LogoutCommand.DATOS_SESION_INVALIDOS,
                    SeguridadApplicationMessages.LogoutCommand.CODIGO_SESION_INVALIDA);
        }
    }
}
