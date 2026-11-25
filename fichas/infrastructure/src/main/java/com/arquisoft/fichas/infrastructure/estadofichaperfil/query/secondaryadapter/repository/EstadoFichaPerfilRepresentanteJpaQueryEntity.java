package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

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
        SELECT efp.id                     AS id,
               efp.ficha_perfil_id        AS ficha_perfil_id,
               ev.representante_comite_id AS representante_comite_id,
               efp.estado_ficha_id        AS estado_id,
               ef.nombre                  AS estado_nombre,
               efp.fecha_actualizacion    AS fecha_actualizacion
        FROM estado_ficha_perfil efp
                 JOIN evaluacion_ficha_perfil ev ON ev.ficha_perfil_id = efp.ficha_perfil_id
                 AND NOT EXISTS (
                     SELECT 1 FROM estado_evaluacion_ficha d
                     WHERE d.evaluacion_ficha_perfil_id = ev.id
                       AND d.estado_evaluacion_id = 'DESCARTADA'
                       AND NOT EXISTS (
                           SELECT 1 FROM estado_evaluacion_ficha n
                           WHERE n.evaluacion_ficha_perfil_id = d.evaluacion_ficha_perfil_id
                             AND (n.fecha_actualizacion > d.fecha_actualizacion
                                  OR (n.fecha_actualizacion = d.fecha_actualizacion AND n.id > d.id))))
                 JOIN estado_ficha ef ON ef.id = efp.estado_ficha_id
        """)
@Synchronize({"estado_ficha_perfil", "evaluacion_ficha_perfil", "estado_evaluacion_ficha", "estado_ficha"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoFichaPerfilRepresentanteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "ficha_perfil_id", columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "representante_comite_id", columnDefinition = "uuid")
    private UUID representanteComiteId;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;

    @Column(name = "fecha_actualizacion")
    private Instant fechaActualizacion;
}
