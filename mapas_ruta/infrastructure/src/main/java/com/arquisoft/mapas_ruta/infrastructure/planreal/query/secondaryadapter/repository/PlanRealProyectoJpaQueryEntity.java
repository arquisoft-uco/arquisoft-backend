package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository;

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

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT p.id                 AS id,
               p.tarea_id           AS tarea_id,
               t.descripcion        AS descripcion_tarea,
               t.mapa_ruta_id       AS mapa_ruta_id,
               m.proyecto_grado_id  AS proyecto_grado_id,
               p.tiempo_real_dias AS tiempo_real_dias,
               p.descripcion_real AS descripcion_real
        FROM plan_real p
        JOIN tarea t     ON t.id = p.tarea_id
        JOIN mapa_ruta m ON m.id = t.mapa_ruta_id
        """)
@Synchronize({"plan_real", "tarea", "mapa_ruta"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanRealProyectoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "tarea_id", columnDefinition = "uuid")
    private UUID tareaId;

    @Column(name = "descripcion_tarea")
    private String descripcionTarea;

    @Column(name = "mapa_ruta_id", columnDefinition = "uuid")
    private UUID mapaRutaId;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "tiempo_real_dias")
    private int tiempoRealDias;

    @Column(name = "descripcion_real")
    private String descripcionReal;
}
