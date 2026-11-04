package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "asesor_proyecto_grado", uniqueConstraints = @UniqueConstraint(
        name = "uk_apg_unica", columnNames = {"asesor_id", "proyecto_grado_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesorProyectoGradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "asesor_id", nullable = false, columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;

    @Column(name = "finalizado_en")
    private Instant finalizadoEn;
}
