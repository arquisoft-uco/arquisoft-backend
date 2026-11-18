package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.secondaryadapter.entity;

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
@Table(name = "entregable_proyecto_grado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregableProyectoGradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "UUID")
    private UUID proyectoGradoId;

    @Column(name = "version_entregable", nullable = false)
    private int versionEntregable;

    @Column(name = "estado_entregable_id", nullable = false, length = 60)
    private String estadoEntregableId;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;
}
