package com.arquisoft.fichas.application.usuario.command;

import com.arquisoft.fichas.application.usuario.command.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.port.in.RegistrarUsuarioInputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegistrarUsuarioUseCase implements RegistrarUsuarioInputPort {

    @Override
    public void ejecutar(RegistrarUsuarioCommand command) {
        // TODO: persistir en tabla espejo fichas_perfil.usuarios_espejo
        log.info(FichasMessages.Usuario.LOG_REGISTRADO_ESPEJO_SIMULADO,
                command.usuarioId(), command.email(), command.rol());
    }
}
