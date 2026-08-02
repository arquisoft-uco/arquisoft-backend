package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.message.UsuariosMessages;
import com.arquisoft.usuarios.application.usuario.command.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.CrearUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.validator.CrearUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.aggregate.UsuarioAggregate;
import com.arquisoft.usuarios.domain.usuario.port.out.UsuarioOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrearUsuarioUseCaseImpl implements CrearUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final CrearUsuarioValidator crearUsuarioValidator;
    private final EventPublisher eventPublisher;

    @Override
    public UUID ejecutar(CrearUsuarioCommand entrada) {
        UsuarioAggregate usuario = UsuarioAggregate.crear(entrada.email(), entrada.rol());

        crearUsuarioValidator.validar(usuario);

        usuarioOutputPort.save(usuario);

        usuario.drainUnPublishedEvents().forEach(eventPublisher::publish);

        log.info(UsuariosMessages.Usuario.LOG_CREADO, usuario.getId(), entrada.email(), entrada.rol().getCode());
        return usuario.getId();
    }
}
