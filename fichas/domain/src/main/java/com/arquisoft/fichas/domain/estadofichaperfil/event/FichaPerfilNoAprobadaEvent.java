package com.arquisoft.fichas.domain.estadofichaperfil.event;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class FichaPerfilNoAprobadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.FICHA_PERFIL_NO_APROBADA;
    public static final String EVENT_TYPE = "FichaPerfilNoAprobadaEvent";

    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final ContactoAsesor asesor;
    private final List<ContactoEstudiante> estudiantes;

    public FichaPerfilNoAprobadaEvent(
            UUID fichaPerfilId,
            String tituloProyecto,
            ContactoAsesor asesor,
            List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.asesor = asesor;
        this.estudiantes = List.copyOf(estudiantes);
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }
}
