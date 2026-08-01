package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.command.port.in.ValidateTokenUseCase;
import com.arquisoft.shared.message.SeguridadMessages;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;
import com.arquisoft.seguridad.domain.auth.port.out.TokenValidationOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateTokenUseCaseImpl implements ValidateTokenUseCase {

    private final TokenValidationOutputPort tokenValidationOutputPort;

    @Override
    public ValidationResult ejecutar(TokenAggregate tokenAggregate) {
        log.debug(SeguridadMessages.Token.VALIDAR_DEBUG);

        try {
            if (tokenValidationOutputPort.validarToken(tokenAggregate.valor())) {
                IdentidadToken identidad = tokenValidationOutputPort.extraerInfo(tokenAggregate.valor());

                return new ValidationResult(
                        true,
                        identidad.identidadId(),
                        identidad.correo(),
                        SeguridadMessages.Token.TOKEN_VALIDO
                );
            } else {
                return new ValidationResult(false, null, null,
                        SeguridadMessages.Token.TOKEN_INVALIDO);
            }
        } catch (Exception e) {
            log.debug(SeguridadMessages.Token.ERROR_VALIDAR, e.getMessage());
            return new ValidationResult(false, null, null,
                    SeguridadMessages.Token.ERROR_VALIDAR_PREFIJO + e.getMessage());
        }
    }
}
