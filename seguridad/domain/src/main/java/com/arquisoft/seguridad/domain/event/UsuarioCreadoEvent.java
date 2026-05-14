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

    /**
     * Topic canónico de este evento. Es la fuente de verdad del contrato de mensajería.
     * Cada bounded context consumidor debe mantener su propia copia de este valor
     * (no puede importar esta clase) — esa duplicación es intencional por aislamiento
     * de contextos. Si este valor cambia, deben actualizarse los bindings de todos
     * los contextos que lo consuman.
     */
    public static final String EVENT_TOPIC = "seguridad.usuario.creado";

    /**
     * Identificador de tipo estable del evento. Desvinculado del nombre de la clase Java
     * para resistir renombres de refactoring sin alterar logs históricos ni contratos de
     * mensajería.
     */
    public static final String EVENT_TYPE = "UsuarioCreadoEvent";

    private final String email;
    private final String rol;

    public UsuarioCreadoEvent(UUID usuarioId, String email, String rol) {
        super(usuarioId.toString(), EVENT_TOPIC, EVENT_TYPE);
        this.email = email;
        this.rol   = rol;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }
}
