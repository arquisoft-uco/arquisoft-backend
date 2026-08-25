package com.arquisoft.usuarios.domain.representantecomitecurriculum.event;

import com.arquisoft.shared.events.DomainEvent;

import java.util.UUID;

public final class RepresentanteComiteCurriculumAgregadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "usuarios.representante_comite_curriculum.agregado";
    public static final String EVENT_TYPE = "RepresentanteComiteCurriculumAgregado";

    private final UUID representanteId;
    private final UUID usuarioId;
    private final String email;
    private final String rol;

    public RepresentanteComiteCurriculumAgregadoEvent(
            UUID representanteId,
            UUID usuarioId,
            String email,
            String rol
    ) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.representanteId = representanteId;
        this.usuarioId = usuarioId;
        this.email = email;
        this.rol = rol;
    }

    public UUID getRepresentanteId() {
        return representanteId;
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
