package com.arquisoft.fichas.domain.fichaperfil.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class FichaPerfilRegistradaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.FICHA_PERFIL_REGISTRADA;
    public static final String EVENT_TYPE = "FichaPerfilRegistradaEvent";

    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final UUID asesorFichaId;
    private final String asesorNombre;
    private final String asesorEmail;
    private final List<DestinatarioEvento> estudiantes;

    public FichaPerfilRegistradaEvent(
            UUID fichaPerfilId,
            String tituloProyecto,
            UUID asesorFichaId,
            String asesorNombre,
            String asesorEmail,
            List<DestinatarioEvento> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.asesorFichaId = asesorFichaId;
        this.asesorNombre = asesorNombre;
        this.asesorEmail = asesorEmail;
        this.estudiantes = List.copyOf(estudiantes);
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

    public String getAsesorNombre() {
        return asesorNombre;
    }

    public String getAsesorEmail() {
        return asesorEmail;
    }

    public List<DestinatarioEvento> getEstudiantes() {
        return estudiantes;
    }
}
