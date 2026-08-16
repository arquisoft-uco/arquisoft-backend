package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.UsuarioKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.CrearUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.validator.CrearUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrearUsuarioUseCaseImpl implements CrearUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final EmailUsuarioExisteFinder emailUsuarioExisteFinder;
    private final CrearUsuarioValidator crearUsuarioValidator;
    private final EventPublisher eventPublisher;
    private final CatalogoMensajes catalogo;

    @Override
    public UUID ejecutar(CrearUsuarioCommand entrada) {
        var usuario = UsuarioDomain.crear(entrada.email(), entrada.rol());

        boolean emailYaExiste = emailUsuarioExisteFinder.obtener(usuario.getEmail());

        crearUsuarioValidator.validar(usuario, emailYaExiste);

        usuarioOutputPort.save(usuario);

        usuario.extraerEventosSinPublicar().forEach(eventPublisher::publish);

        log.info(catalogo.obtener(UsuarioKey.LOG_CREADO), usuario.getId(), entrada.email(), entrada.rol().getCodigo());
        return usuario.getId();
    }
}
