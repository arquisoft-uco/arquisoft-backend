package com.arquisoft.entregables.infrastructure.paqueteentregable.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT p.id                           AS id,
               p.entregable_proyecto_grado_id AS entregable_proyecto_grado_id,
               p.bucket                       AS bucket,
               p.clave_objeto                 AS clave_objeto,
               p.nombre_archivo               AS nombre_archivo
        FROM paquete_entregable p
        """)
@Synchronize("paquete_entregable")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteEntregableAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "entregable_proyecto_grado_id")
    private UUID entregableProyectoGradoId;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "clave_objeto")
    private String claveObjeto;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;
}
