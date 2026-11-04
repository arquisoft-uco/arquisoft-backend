package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "estado_proyecto_grado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoProyectoGradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "UUID")
    private UUID proyectoGradoId;

    @Column(name = "estado_proyecto_id", nullable = false, length = 60)
    private String estadoProyectoId;

    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;
}
