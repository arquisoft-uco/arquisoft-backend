package com.arquisoft.seguridad.domain.usuario.event;

import com.arquisoft.shared.events.DomainEvent;

import java.util.UUID;

public class UsuarioCreadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "seguridad.usuario.creado";
    public static final String EVENT_TYPE = "UsuarioCreadoEvent";

    private final UUID usuarioId;
    private final String email;
    private final String rol;

    public UsuarioCreadoEvent(UUID usuarioId, String email, String rol) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.usuarioId = usuarioId;
        this.email     = email;
        this.rol       = rol;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }
}
