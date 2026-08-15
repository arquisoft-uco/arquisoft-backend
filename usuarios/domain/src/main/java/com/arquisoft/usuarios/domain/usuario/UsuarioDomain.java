package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.events.AggregateRoot;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.UsuarioKey;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.usuarios.domain.usuario.event.UsuarioCreadoEvent;

import java.util.UUID;

public final class UsuarioDomain extends AggregateRoot {

    private final UUID id;
    private final String email;
    private final UsuarioRole rol;

    private UsuarioDomain(UUID id, String email, UsuarioRole rol) {
        this.id    = id;
        this.email = email;
        this.rol   = rol;
    }

    public static UsuarioDomain crear(String email, UsuarioRole rol) {
        if (email == null || email.isBlank()) {
            throw new DomainException(
                    Mensajes.obtener(UsuarioKey.ERROR_EMAIL_REQUERIDO),
                    UsuariosCodes.Usuario.EMAIL_REQUERIDO);
        }
        if (rol == null) {
            throw new DomainException(
                    Mensajes.obtener(UsuarioKey.ERROR_ROL_REQUERIDO),
                    UsuariosCodes.Usuario.ROL_REQUERIDO);
        }

        UsuarioDomain usuario = new UsuarioDomain(UUID.randomUUID(), email.trim().toLowerCase(), rol);
        usuario.publicarEvento(new UsuarioCreadoEvent(usuario.id, usuario.email, usuario.rol.getCodigo()));
        return usuario;
    }

    public static UsuarioDomain reconstruir(UUID id, String email, UsuarioRole rol) {
        return new UsuarioDomain(id, email, rol);
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
