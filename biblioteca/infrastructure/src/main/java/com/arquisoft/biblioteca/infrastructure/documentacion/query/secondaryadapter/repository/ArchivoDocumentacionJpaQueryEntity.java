package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT d.id                      AS id,
               d.estado_documentacion_id AS estado_id,
               e.clave_objeto            AS clave_objeto,
               e.nombre_archivo          AS nombre_archivo
        FROM documentacion d
                 JOIN entregable_proyecto_grado e ON e.id = d.entregable_proyecto_grado_id
        """)
@Synchronize({"documentacion", "entregable_proyecto_grado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivoDocumentacionJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "clave_objeto")
    private String claveObjeto;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;
}
