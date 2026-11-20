package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Immutable
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

    @Column(name = "estado_entregable_nombre", nullable = false, length = 60)
    private String estadoEntregableNombre;

    @Column(name = "bucket", length = 63)
    private String bucket;

    @Column(name = "clave_objeto", length = 2000)
    private String claveObjeto;

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;
}
