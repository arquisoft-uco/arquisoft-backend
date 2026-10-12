package com.arquisoft.usuarios.domain.administrador.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class AdministradorRemovidoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Usuarios.ADMINISTRADOR_REMOVIDO;
    public static final String EVENT_TYPE = "AdministradorRemovidoEvent";

    private final UUID usuario;
    private final String identificador;
    private final String nombre;
    private final String email;

    public AdministradorRemovidoEvent(UUID usuario, String identificador, String nombre, String email) {
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
