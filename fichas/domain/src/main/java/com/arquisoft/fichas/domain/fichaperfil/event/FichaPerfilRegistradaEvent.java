package com.arquisoft.fichas.domain.fichaperfil.event;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class FichaPerfilRegistradaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.FICHA_PERFIL_REGISTRADA;
    public static final String EVENT_TYPE = "FichaPerfilRegistradaEvent";

    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final UUID asesorFichaId;
    private final ContactoAsesor asesor;

    public FichaPerfilRegistradaEvent(
            UUID fichaPerfilId,
            String tituloProyecto,
            UUID asesorFichaId,
            ContactoAsesor asesor) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.asesorFichaId = asesorFichaId;
        this.asesor = asesor;
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public UUID getAsesorFichaId() {
        return asesorFichaId;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }
}
