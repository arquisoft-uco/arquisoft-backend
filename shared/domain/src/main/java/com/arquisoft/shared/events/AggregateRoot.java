package com.arquisoft.shared.events;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
    private final List<DomainEvent> eventosSinPublicar = new ArrayList<>();

    public void publicarEvento(DomainEvent evento) {
        eventosSinPublicar.add(evento);
    }

    public List<DomainEvent> extraerEventosSinPublicar() {
        List<DomainEvent> extraidos = new ArrayList<>(eventosSinPublicar);
        eventosSinPublicar.clear();
        return extraidos;
    }

    protected List<DomainEvent> obtenerEventosSinPublicar() {
        return new ArrayList<>(eventosSinPublicar);
    }
}
