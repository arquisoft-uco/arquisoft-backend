package com.arquisoft.fichas.application.fichaperfil.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementación del caso de uso para registrar un usuario en el contexto de fichas.
 *
 * <p><b>Estado actual (simulado):</b> solo registra la recepción del evento con un log
 * de auditoría. No persiste en base de datos.
 *
 * <p><b>TODO:</b> persistir el usuario en una tabla espejo {@code fichas_perfil.usuarios_espejo}
 * cuando se implemente la HU correspondiente.
 */
@Slf4j
@Component
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioInputPort {

    @Override
    public void registrar(UUID usuarioId, String email, String rol) {
        // TODO: persistir en tabla espejo fichas_perfil.usuarios_espejo
        log.info("[FICHAS] Usuario registrado en espejo (simulado): usuarioId={} email={} rol={}",
                usuarioId, email, rol);
    }
}
