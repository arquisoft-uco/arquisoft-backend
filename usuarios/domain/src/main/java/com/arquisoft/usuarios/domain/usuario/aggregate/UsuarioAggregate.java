package com.arquisoft.usuarios.domain.usuario.aggregate;

import com.arquisoft.shared.events.AggregateRoot;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.usuarios.domain.usuario.event.UsuarioCreadoEvent;

import java.util.UUID;

public final class UsuarioAggregate extends AggregateRoot {

    private final UUID id;
    private final String email;
    private final UsuarioRole rol;

    private UsuarioAggregate(UUID id, String email, UsuarioRole rol) {
        this.id    = id;
        this.email = email;
        this.rol   = rol;
    }

    public static UsuarioAggregate crear(String email, UsuarioRole rol) {
        if (email == null || email.isBlank()) {
            throw new DomainException("El email del usuario no puede ser vacio", "USUARIO_EMAIL_REQUERIDO");
        }
        if (rol == null) {
            throw new DomainException("El rol del usuario no puede ser nulo", "USUARIO_ROL_REQUERIDO");
        }

        UsuarioAggregate usuario = new UsuarioAggregate(UUID.randomUUID(), email.trim().toLowerCase(), rol);
        usuario.publishEvent(new UsuarioCreadoEvent(usuario.id, usuario.email, usuario.rol.getCode()));
        return usuario;
    }

    public static UsuarioAggregate reconstruir(UUID id, String email, UsuarioRole rol) {
        return new UsuarioAggregate(id, email, rol);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UsuarioRole getRol() {
        return rol;
    }
}
