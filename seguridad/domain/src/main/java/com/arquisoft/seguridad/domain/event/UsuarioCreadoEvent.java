package com.arquisoft.seguridad.domain.event;

import com.arquisoft.shared.events.DomainEvent;

import java.util.UUID;

/**
 * Evento de dominio publicado cuando un usuario es creado en el sistema.
 *
 * <p>El {@code aggregateId} es el UUID del usuario recién creado.
 * Este evento es la fuente de verdad para que otros contextos (fichas, proyectos, etc.)
 * registren al usuario en sus propias bases de datos como tabla espejo.
 *
 * <p>Topic: {@code seguridad.usuario.creado}
 * Formato: {@code {contexto}.{entidad}.{accion}}
 */
public class UsuarioCreadoEvent extends DomainEvent {

    private final String email;
    private final String rol;

    public UsuarioCreadoEvent(UUID usuarioId, String email, String rol) {
        super(usuarioId.toString());
        this.email = email;
        this.rol   = rol;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public String getEventTopic() {
        return "seguridad.usuario.creado";
    }
}
