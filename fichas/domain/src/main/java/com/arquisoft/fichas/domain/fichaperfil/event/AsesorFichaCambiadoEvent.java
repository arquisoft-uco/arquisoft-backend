package com.arquisoft.fichas.domain.fichaperfil.event;

import com.arquisoft.shared.events.DomainEvent;

import java.util.UUID;

public class AsesorFichaCambiadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "fichas.ficha_perfil.asesor_cambiado";
    public static final String EVENT_TYPE = "AsesorFichaCambiadoEvent";

    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final UUID asesorFichaId;
    private final String asesorNombre;
    private final String asesorEmail;

    public AsesorFichaCambiadoEvent(
            UUID fichaPerfilId,
            String tituloProyecto,
            UUID asesorFichaId,
            String asesorNombre,
            String asesorEmail) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.asesorFichaId = asesorFichaId;
        this.asesorNombre = asesorNombre;
        this.asesorEmail = asesorEmail;
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
}
