package com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.entity;

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
    private UUID entregableProyectoGradoId;

    @Column(name = "bucket", nullable = false, length = 63)
    private String bucket;

    @Column(name = "clave_objeto", nullable = false, length = 2000)
    private String claveObjeto;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "tamano_bytes", nullable = false)
    private long tamanoBytes;

    @Column(name = "generado_en", nullable = false)
    private Instant generadoEn;
}
