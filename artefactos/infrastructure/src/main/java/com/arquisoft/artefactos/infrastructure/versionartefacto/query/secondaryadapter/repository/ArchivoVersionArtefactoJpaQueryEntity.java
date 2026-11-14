package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

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
        SELECT va.id                AS id,
               va.uri_archivo       AS uri_archivo,
               va.version_artefacto AS version,
               a.asesor_id          AS asesor_id,
               vra.nombre_documento AS nombre_documento,
               pg.nombre            AS nombre_proyecto,
               pg.estado_proyecto_id IN ('FINALIZADO', 'CANCELADO') AS proyecto_finalizado
        FROM version_artefacto va
                 JOIN artefacto a ON a.id = va.artefacto_id
                 JOIN version_repositorio_artefacto vra ON vra.id = a.version_repositorio_artefacto_id
                 JOIN proyecto_grado pg ON pg.id = a.proyecto_grado_id
        """)
@Synchronize({"version_artefacto", "artefacto", "version_repositorio_artefacto", "proyecto_grado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArchivoVersionArtefactoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "uri_archivo")
    private String uriArchivo;

    @Column(name = "version")
    private int version;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "nombre_documento")
    private String nombreDocumento;

    @Column(name = "nombre_proyecto")
    private String nombreProyecto;

    @Column(name = "proyecto_finalizado")
    private boolean proyectoFinalizado;
}
