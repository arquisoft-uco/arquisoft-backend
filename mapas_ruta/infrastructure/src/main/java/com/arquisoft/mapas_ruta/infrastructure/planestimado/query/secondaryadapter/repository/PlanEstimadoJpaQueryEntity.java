package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository;

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
        SELECT p.id                   AS id,
               p.tarea_id             AS tarea_id,
               p.tiempo_estimado_dias AS tiempo_estimado_dias,
               p.descripcion_estimado AS descripcion_estimado
        FROM plan_estimado p
        """)
@Synchronize({"plan_estimado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanEstimadoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "tarea_id", columnDefinition = "uuid")
    private UUID tareaId;

    @Column(name = "tiempo_estimado_dias")
    private int tiempoEstimadoDias;

    @Column(name = "descripcion_estimado")
    private String descripcionEstimado;
}
