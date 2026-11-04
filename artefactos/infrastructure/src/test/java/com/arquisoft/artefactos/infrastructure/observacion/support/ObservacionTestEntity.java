package com.arquisoft.artefactos.infrastructure.observacion.support;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "observacion")
public class ObservacionTestEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "revision_asesor_id")
    private UUID revisionAsesorId;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Column(name = "estado_observacion_id")
    private String estadoObservacionId;

    protected ObservacionTestEntity() {}

    public ObservacionTestEntity(UUID id, UUID revisionAsesorId, String descripcion, String estadoObservacionId) {
        this.id = id;
        this.revisionAsesorId = revisionAsesorId;
        this.descripcion = descripcion;
        this.estadoObservacionId = estadoObservacionId;
    }
}
