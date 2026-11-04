package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

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

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT m.id                AS id,
               m.proyecto_grado_id AS proyecto_grado_id,
               p.titulo_proyecto   AS titulo_proyecto,
               m.fecha_inicio      AS fecha_inicio,
               m.fecha_fin         AS fecha_fin
        FROM mapa_ruta m
                 JOIN proyecto_grado p ON p.id = m.proyecto_grado_id
        """)
@Synchronize({"mapa_ruta", "proyecto_grado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapaRutaEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
}
