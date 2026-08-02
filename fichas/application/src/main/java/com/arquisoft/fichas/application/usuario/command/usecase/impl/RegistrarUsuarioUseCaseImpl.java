package com.arquisoft.fichas.application.usuario.command.usecase.impl;

import com.arquisoft.fichas.application.usuario.command.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final AppLogger logger;

    @Override
    public void ejecutar(RegistrarUsuarioCommand entrada) {
        // TODO: persistir en tabla espejo fichas_perfil.usuarios_espejo
        logger.info(FichasMessages.Usuario.LOG_REGISTRADO_ESPEJO_SIMULADO,
                entrada.usuarioId(), entrada.email(), entrada.rol());
    }
}
