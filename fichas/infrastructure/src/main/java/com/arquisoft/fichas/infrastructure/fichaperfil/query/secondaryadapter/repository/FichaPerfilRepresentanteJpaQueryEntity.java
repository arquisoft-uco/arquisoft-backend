package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

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

import java.time.Instant;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT e.id                      AS id,
               e.representante_comite_id AS representante_comite_id,
               f.id                      AS ficha_id,
               f.titulo_proyecto         AS titulo_proyecto,
               a.id                      AS asesor_id,
               a.identificador           AS asesor_identificador,
               a.nombre                  AS asesor_nombre,
               a.email                   AS asesor_email,
               ef.estado_ficha_id        AS estado_id,
               ec.nombre                 AS estado_nombre,
               ef.fecha_actualizacion    AS estado_fecha_actualizacion,
               ee.estado_evaluacion_id   AS evaluacion_estado_id,
               ev.nombre                 AS evaluacion_estado_nombre
        FROM evaluacion_ficha_perfil e
                 JOIN ficha_perfil f ON f.id = e.ficha_perfil_id
                 JOIN asesor_ficha a ON a.id = f.asesor_ficha_id
                 JOIN estado_ficha_perfil ef ON ef.ficha_perfil_id = f.id
                 JOIN estado_ficha ec ON ec.id = ef.estado_ficha_id
                 JOIN estado_evaluacion_ficha ee ON ee.evaluacion_ficha_perfil_id = e.id
                 JOIN estado_evaluacion ev ON ev.id = ee.estado_evaluacion_id
        WHERE NOT EXISTS (SELECT 1
                          FROM estado_ficha_perfil n
                          WHERE n.ficha_perfil_id = ef.ficha_perfil_id
                            AND (n.fecha_actualizacion > ef.fecha_actualizacion
                                 OR (n.fecha_actualizacion = ef.fecha_actualizacion AND n.id > ef.id)))
          AND NOT EXISTS (SELECT 1
                          FROM estado_evaluacion_ficha m
                          WHERE m.evaluacion_ficha_perfil_id = ee.evaluacion_ficha_perfil_id
                            AND (m.fecha_actualizacion > ee.fecha_actualizacion
                                 OR (m.fecha_actualizacion = ee.fecha_actualizacion AND m.id > ee.id)))
        """)
@Synchronize({"evaluacion_ficha_perfil", "ficha_perfil", "asesor_ficha", "estado_ficha_perfil", "estado_ficha",
        "estado_evaluacion_ficha", "estado_evaluacion"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaPerfilRepresentanteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "representante_comite_id", columnDefinition = "uuid")
    private UUID representanteComiteId;

    @Column(name = "ficha_id", columnDefinition = "uuid")
    private UUID fichaId;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "asesor_identificador")
    private String asesorIdentificador;

    @Column(name = "asesor_nombre")
    private String asesorNombre;

    @Column(name = "asesor_email")
    private String asesorEmail;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;

    @Column(name = "estado_fecha_actualizacion")
    private Instant estadoFechaActualizacion;

    @Column(name = "evaluacion_estado_id")
    private String evaluacionEstadoId;

    @Column(name = "evaluacion_estado_nombre")
    private String evaluacionEstadoNombre;
}
