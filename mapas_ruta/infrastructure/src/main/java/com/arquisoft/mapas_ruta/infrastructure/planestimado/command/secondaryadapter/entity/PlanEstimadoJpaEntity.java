package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.entity;

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
@Table(name = "plan_estimado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanEstimadoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "tarea_id", nullable = false, columnDefinition = "uuid")
    private UUID tareaId;

    @Column(name = "tiempo_estimado_dias", nullable = false)
    private int tiempoEstimadoDias;

    @Column(name = "descripcion_estimado", length = 200)
    private String descripcionEstimado;
}
