package com.arquisoft.fichas.application.usuario.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.UsuarioEspejoKey;
import com.arquisoft.fichas.application.usuario.command.primaryport.model.RegistrarUsuarioEspejoCommand;
import com.arquisoft.fichas.application.usuario.command.usecase.RegistrarUsuarioEspejoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioEspejoUseCaseImpl implements RegistrarUsuarioEspejoUseCase {

    private final AppLogger logger;

    @Override
    public void ejecutar(RegistrarUsuarioEspejoCommand entrada) {
        // TODO: persistir en tabla espejo fichas_perfil.usuarios_espejo
        logger.info(UsuarioEspejoKey.LOG_REGISTRADO_ESPEJO_SIMULADO,
                entrada.usuarioId(), UtilTexto.enmascararCorreo(entrada.email()), entrada.rol());
    }
}
