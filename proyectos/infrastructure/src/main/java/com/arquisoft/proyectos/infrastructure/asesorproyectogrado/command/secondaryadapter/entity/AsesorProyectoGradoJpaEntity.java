package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity;

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
@Table(name = "asesor_proyecto_grado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesorProyectoGradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "asesor_id", nullable = false, columnDefinition = "UUID")
    private UUID asesorId;

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "UUID")
    private UUID proyectoGradoId;

    @Column(name = "estado_asesor_id", nullable = false, length = 60)
    private String estadoAsesorId;

    @Column(name = "fecha_asignado", nullable = false)
    private Instant fechaAsignado;
}
