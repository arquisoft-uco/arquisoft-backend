package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.command.port.in.ValidateTokenInputPort;
import com.arquisoft.seguridad.application.util.message.SeguridadApplicationMessages;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;
import com.arquisoft.seguridad.domain.auth.port.out.TokenValidationOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateTokenUseCase implements ValidateTokenInputPort {

    private final TokenValidationOutputPort tokenValidationOutputPort;

    @Override
    public ValidationResult ejecutar(TokenAggregate tokenAggregate) {
        log.debug(SeguridadApplicationMessages.ValidateTokenUseCase.VALIDAR_DEBUG);

        try {
            if (tokenValidationOutputPort.validarToken(tokenAggregate.valor())) {
                IdentidadToken identidad = tokenValidationOutputPort.extraerInfo(tokenAggregate.valor());

                return new ValidationResult(
                        true,
                        identidad.identidadId(),
                        identidad.correo(),
                        SeguridadApplicationMessages.ValidateTokenUseCase.TOKEN_VALIDO
                );
            } else {
                return new ValidationResult(false, null, null,
                        SeguridadApplicationMessages.ValidateTokenUseCase.TOKEN_INVALIDO);
            }
        } catch (Exception e) {
            log.debug(SeguridadApplicationMessages.ValidateTokenUseCase.ERROR_VALIDAR, e.getMessage());
            return new ValidationResult(false, null, null,
                    SeguridadApplicationMessages.ValidateTokenUseCase.ERROR_VALIDAR_PREFIJO + e.getMessage());
        }
    }
}
