package com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.secondaryadapter.entity;

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
@Table(name = "entregable_proyecto_acceso")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregableProyectoAccesoJpaEntity {

    @Id
    @Column(name = "entregable_id")
    private UUID entregable;

    @Column(name = "proyecto_id", nullable = false)
    private UUID proyecto;

    @Column(name = "version_entregable", nullable = false)
    private int versionEntregable;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;
}
