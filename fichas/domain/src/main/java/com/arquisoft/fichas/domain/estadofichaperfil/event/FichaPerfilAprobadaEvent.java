package com.arquisoft.fichas.domain.estadofichaperfil.event;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class FichaPerfilAprobadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.FICHA_PERFIL_APROBADA;
    public static final String EVENT_TYPE = "FichaPerfilAprobadaEvent";

    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final String estadoFicha;
    private final UUID coordinadorId;
    private final ContactoAsesor asesor;
    private final List<IntegranteFicha> estudiantes;

    public FichaPerfilAprobadaEvent(
            UUID fichaPerfilId,
            String tituloProyecto,
            String estadoFicha,
            UUID coordinadorId,
            ContactoAsesor asesor,
            List<IntegranteFicha> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.estadoFicha = estadoFicha;
        this.coordinadorId = coordinadorId;
        this.asesor = asesor;
        this.estudiantes = List.copyOf(estudiantes);
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public String getEstadoFicha() {
        return estadoFicha;
    }

    public UUID getCoordinadorId() {
        return coordinadorId;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }

    public List<IntegranteFicha> getEstudiantes() {
        return estudiantes;
    }
}
