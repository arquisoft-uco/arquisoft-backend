package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.time.Instant;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT e.id                           AS id,
               e.entregable_proyecto_grado_id AS entregable_proyecto_grado_id,
               e.estado_entregable_id         AS estado_entregable_id,
               ee.nombre                      AS estado_entregable_nombre,
               e.fecha_actualizacion          AS fecha_actualizacion
        FROM estado_entregable_proyecto_grado e
                 JOIN estado_entregable ee ON ee.id = e.estado_entregable_id
        """)
@Synchronize({"estado_entregable_proyecto_grado", "estado_entregable"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoEntregableProyectoGradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "entregable_proyecto_grado_id")
    private UUID entregableProyectoGradoId;

    @Column(name = "estado_entregable_id")
    private String estadoEntregableId;

    @Column(name = "estado_entregable_nombre")
    private String estadoEntregableNombre;

    @Column(name = "fecha_actualizacion")
    private Instant fechaActualizacion;
}
