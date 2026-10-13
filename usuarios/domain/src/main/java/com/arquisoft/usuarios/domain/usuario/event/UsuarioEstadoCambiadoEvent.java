package com.arquisoft.usuarios.domain.usuario.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class UsuarioEstadoCambiadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Usuarios.USUARIO_ESTADO_CAMBIADO;
    public static final String EVENT_TYPE = "UsuarioEstadoCambiadoEvent";

    private final UUID usuario;
    private final String nombre;
    private final String email;
    private final String estado;
    private final String estadoNombre;

    public UsuarioEstadoCambiadoEvent(UUID usuario, String nombre, String email, String estado,
                                      String estadoNombre) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.usuario = usuario;
        this.nombre = nombre;
        this.email = email;
        this.estado = estado;
        this.estadoNombre = estadoNombre;
    }

    public UUID getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getEstado() {
        return estado;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }
}
