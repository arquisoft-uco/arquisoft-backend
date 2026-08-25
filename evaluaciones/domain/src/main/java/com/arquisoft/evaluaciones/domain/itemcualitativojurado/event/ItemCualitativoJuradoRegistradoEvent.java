package com.arquisoft.evaluaciones.domain.itemcualitativojurado.event;

import com.arquisoft.shared.events.DomainEvent;

import java.util.UUID;

public final class ItemCualitativoJuradoRegistradoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "evaluaciones.item_cualitativo_jurado.registrado";
    public static final String EVENT_TYPE = "ItemCualitativoJuradoRegistradoEvent";

    private final UUID itemCualitativoJuradoId;
    private final String nombre;
    private final String descripcion;

    public ItemCualitativoJuradoRegistradoEvent(
            UUID itemCualitativoJuradoId, String nombre, String descripcion) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.itemCualitativoJuradoId = itemCualitativoJuradoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public UUID getItemCualitativoJuradoId() {
        return itemCualitativoJuradoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
