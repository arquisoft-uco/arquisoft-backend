package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.entity;

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
@Table(name = "paquete_entregable")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaqueteEntregableJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "entregable_proyecto_grado_id", nullable = false, columnDefinition = "UUID")
    private UUID entregableProyectoGrado;

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "UUID")
    private UUID proyectoGrado;

    @Column(name = "version_entregable", nullable = false)
    private int versionEntregable;

    @Column(name = "clave_objeto", nullable = false, length = 2000)
    private String claveObjeto;

    @Column(name = "tamano_bytes", nullable = false)
    private long tamanoBytes;

    @Column(name = "generado_en", nullable = false)
    private Instant generadoEn;
}
