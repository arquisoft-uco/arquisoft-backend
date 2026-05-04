package com.arquisoft.fichas.domain.event;

import com.arquisoft.shared.domain.DomainEvent;

/**
 * Evento de dominio emitido cuando se crea una nueva FichaPerfil.
 *
 * <p>Definido en HU-160 (primera HU del contexto fichas) aunque este use case
 * es de consulta. Quedará disponible para futuras HUs de escritura que invoquen
 * {@code FichaPerfil.build(...)}.
 */
public final class FichaPerfilCreadaEvent extends DomainEvent {

    private final String tituloProyecto;

    public FichaPerfilCreadaEvent(String aggregateId, String tituloProyecto) {
        super(aggregateId);
        this.tituloProyecto = tituloProyecto;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    @Override
    public String getRoutingKey() {
        return "fichas.ficha_perfil.creada";
    }
}
