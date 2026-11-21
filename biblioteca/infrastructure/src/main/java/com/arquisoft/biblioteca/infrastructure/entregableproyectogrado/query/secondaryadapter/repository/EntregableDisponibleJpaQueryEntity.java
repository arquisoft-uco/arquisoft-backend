package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

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
        SELECT e.id                                 AS id,
               e.proyecto_grado_id                  AS proyecto_grado_id,
               p.titulo                             AS titulo_proyecto,
               CASE WHEN p.titulo IS NULL THEN 1 ELSE 0 END AS titulo_ausente,
               CAST(e.version_entregable AS BIGINT) AS version_entregable,
               e.nombre_archivo                     AS nombre_archivo
        FROM entregable_proyecto_grado e
                 LEFT JOIN proyecto_grado p ON p.id = e.proyecto_grado_id
        WHERE e.clave_objeto IS NOT NULL AND TRIM(e.clave_objeto) <> ''
          AND NOT EXISTS (SELECT 1 FROM documentacion d WHERE d.entregable_proyecto_grado_id = e.id)
        """)
@Synchronize({"entregable_proyecto_grado", "proyecto_grado", "documentacion"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregableDisponibleJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "titulo_ausente")
    private Integer tituloAusente;

    @Column(name = "version_entregable")
    private Long versionEntregable;

    @Column(name = "nombre_archivo")
    private String nombreArchivo;
}
