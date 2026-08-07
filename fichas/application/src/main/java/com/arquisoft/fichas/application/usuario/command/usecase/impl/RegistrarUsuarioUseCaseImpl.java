package com.arquisoft.fichas.application.usuario.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.UsuarioKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.usuario.command.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void ejecutar(RegistrarUsuarioCommand entrada) {
        // TODO: persistir en tabla espejo fichas_perfil.usuarios_espejo
        logger.info(catalogo.obtener(UsuarioKey.LOG_REGISTRADO_ESPEJO_SIMULADO),
                entrada.usuarioId(), entrada.email(), entrada.rol());
    }
}
