package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.usuarios.UsuarioKey;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.mapper.UsuarioMapper;
import com.arquisoft.usuarios.application.usuario.command.usecase.CrearUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.validator.CrearUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.event.UsuarioCreadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CrearUsuarioUseCaseImpl implements CrearUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final EmailUsuarioExisteFinder emailUsuarioExisteFinder;
    private final CrearUsuarioValidator crearUsuarioValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(CrearUsuarioCommand entrada) {
        var usuario = UsuarioDomain.crear(entrada.email(), entrada.rol());

        boolean emailYaExiste = emailUsuarioExisteFinder.obtener(usuario.getEmail());

        crearUsuarioValidator.validar(usuario, emailYaExiste);

        usuarioOutputPort.guardar(UsuarioMapper.toEntity(usuario));

        eventPublisher.publish(new UsuarioCreadoEvent(
                usuario.getId(), usuario.getEmail(), usuario.getRol().getCodigo()));

        logger.info(
                Mensajes.obtener(UsuarioKey.LOG_CREADO),
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().getCodigo());
        return usuario.getId();
    }
}
