package com.arquisoft.fichas.application.usuario.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.UsuarioEspejoKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final AppLogger logger;

    @Override
    public void ejecutar(RegistrarUsuarioCommand entrada) {
        // TODO: persistir en tabla espejo fichas_perfil.usuarios_espejo
        logger.info(Mensajes.obtener(UsuarioEspejoKey.LOG_REGISTRADO_ESPEJO_SIMULADO),
                entrada.usuarioId(), entrada.email(), entrada.rol());
    }
}
