package com.arquisoft.fichas.application.usuario.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.UsuarioKey;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.fichas.application.usuario.command.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void ejecutar(RegistrarUsuarioCommand entrada) {
        // TODO: persistir en tabla espejo fichas_perfil.usuarios_espejo
        logger.info(catalog.obtener(UsuarioKey.LOG_REGISTRADO_ESPEJO_SIMULADO),
                entrada.usuarioId(), entrada.email(), entrada.rol());
    }
}
