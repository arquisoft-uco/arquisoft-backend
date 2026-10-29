package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity;

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
@Table(name = "estudiante_proyecto_grado", uniqueConstraints = @UniqueConstraint(
        name = "uk_epg_unica", columnNames = {"estudiante_id", "proyecto_grado_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteProyectoGradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "estudiante_id", nullable = false, columnDefinition = "uuid")
    private UUID estudianteId;

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;

    @Column(name = "destituido_en")
    private Instant destituidoEn;
}
