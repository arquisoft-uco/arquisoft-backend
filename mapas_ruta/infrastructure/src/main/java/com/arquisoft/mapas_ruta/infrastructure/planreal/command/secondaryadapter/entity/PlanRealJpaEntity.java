package com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "plan_real")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanRealJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "tarea_id", nullable = false, columnDefinition = "uuid")
    private UUID tareaId;

    @Column(name = "tiempo_real_dias", nullable = false)
    private int tiempoRealDias;

    @Column(name = "descripcion_real", length = 200)
    private String descripcionReal;
}
