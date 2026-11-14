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
        SELECT p.id               AS id,
               p.tarea_id         AS tarea_id,
               p.tiempo_real_dias AS tiempo_real_dias,
               p.descripcion_real AS descripcion_real
        FROM plan_real p
        """)
@Synchronize({"plan_real"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanRealJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "tarea_id", columnDefinition = "uuid")
    private UUID tareaId;

    @Column(name = "tiempo_real_dias")
    private int tiempoRealDias;

    @Column(name = "descripcion_real")
    private String descripcionReal;
}
