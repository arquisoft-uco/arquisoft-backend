package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity;

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
    private UUID proyectoGrado;

    @Column(name = "version_entregable", nullable = false)
    private int version;

    @Column(name = "asesor_id", nullable = false, columnDefinition = "UUID")
    private UUID asesor;

    @Column(name = "estado_entregable_id", nullable = false, length = 60)
    private String estadoEntregable;
}
