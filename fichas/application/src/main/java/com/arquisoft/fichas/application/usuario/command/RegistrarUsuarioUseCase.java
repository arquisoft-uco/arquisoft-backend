package com.arquisoft.fichas.application.usuario.command;

import com.arquisoft.fichas.application.usuario.command.model.RegistrarUsuarioCommand;
import com.arquisoft.fichas.application.usuario.command.port.in.RegistrarUsuarioInputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegistrarUsuarioUseCase implements RegistrarUsuarioInputPort {

    @Override
    public void ejecutar(RegistrarUsuarioCommand command) {
        // TODO: persistir en tabla espejo fichas_perfil.usuarios_espejo
        log.info("[FICHAS] Usuario registrado en espejo (simulado): usuarioId={} email={} rol={}",
                command.usuarioId(), command.email(), command.rol());
    }
}
