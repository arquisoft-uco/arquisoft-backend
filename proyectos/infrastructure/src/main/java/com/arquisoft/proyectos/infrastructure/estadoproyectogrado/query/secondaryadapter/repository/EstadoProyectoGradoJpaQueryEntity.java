package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
        SELECT epg.id                  AS id,
               epg.proyecto_grado_id   AS proyecto_grado_id,
               epg.estado_proyecto_id  AS estado_proyecto,
               ep.nombre               AS estado_proyecto_nombre,
               epg.fecha_actualizacion AS fecha_actualizacion
        FROM estado_proyecto_grado epg
        JOIN estado_proyecto ep ON ep.id = epg.estado_proyecto_id
        """)
@Synchronize({"estado_proyecto_grado", "estado_proyecto"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoProyectoGradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "estado_proyecto")
    private String estadoProyecto;

    @Column(name = "estado_proyecto_nombre")
    private String estadoProyectoNombre;

    @Column(name = "fecha_actualizacion")
    private Instant fechaActualizacion;
}
