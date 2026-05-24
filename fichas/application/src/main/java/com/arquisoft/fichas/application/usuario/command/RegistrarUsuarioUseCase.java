package com.arquisoft.fichas.application.usuario.command;

import com.arquisoft.fichas.application.usuario.command.port.in.RegistrarUsuarioInputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrarUsuarioUseCase implements RegistrarUsuarioInputPort {

    @Override
    public void registrar(UUID usuarioId, String email, String rol) {
        // TODO: persistir en tabla espejo fichas_perfil.usuarios_espejo
        log.info("[FICHAS] Usuario registrado en espejo (simulado): usuarioId={} email={} rol={}",
                usuarioId, email, rol);
    }
}
