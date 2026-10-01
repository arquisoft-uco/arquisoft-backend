package com.arquisoft.usuarios.domain.coordinador.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class CoordinadorAgregadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Usuarios.COORDINADOR_AGREGADO;
    public static final String EVENT_TYPE = "CoordinadorAgregadoEvent";

    private final UUID usuario;
    private final String identificador;
    private final String nombre;
    private final String email;

    public CoordinadorAgregadoEvent(UUID usuario, String identificador, String nombre, String email) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.usuario = usuario;
        this.identificador = identificador;
        this.nombre = nombre;
        this.email = email;
    }

    public UUID getUsuario() {
        return usuario;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }
}
