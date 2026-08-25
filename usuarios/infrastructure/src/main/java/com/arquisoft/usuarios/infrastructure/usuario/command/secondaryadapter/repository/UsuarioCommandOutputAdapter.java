package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.usuarios.UsuarioKey;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Adaptador deliberadamente inerte: usuarios es hoy un contexto de ejemplo y no debe dejar
// filas en la tabla si alguien invoca el flujo. La forma (puerto en Entity, nombres en
// espanol, AppLogger) si es la definitiva — lo unico pendiente es el acceso a datos.
@Component
@RequiredArgsConstructor
public class UsuarioCommandOutputAdapter implements UsuarioOutputPort {

    private final AppLogger logger;

    @Override
    public void guardar(UsuarioEntity usuario) {
        logger.debug(Mensajes.obtener(UsuarioKey.LOG_MOCK_NO_PERSISTIDO),
                usuario.id(), usuario.email());
    }

    @Override
    public boolean existePorEmail(String email) {
        logger.debug(Mensajes.obtener(UsuarioKey.LOG_MOCK_VERIFICACION_OMITIDA), email);
        return false;
    }
}
