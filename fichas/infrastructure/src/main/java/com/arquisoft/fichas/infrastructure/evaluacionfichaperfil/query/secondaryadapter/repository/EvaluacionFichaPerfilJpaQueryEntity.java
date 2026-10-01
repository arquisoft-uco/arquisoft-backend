package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

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
               efp.representante_comite_id AS representante_comite_id,
               efp.fecha_creacion         AS fecha_creacion,
               eef.estado_evaluacion_id   AS estado_evaluacion_id,
               ee.nombre                  AS estado_evaluacion_nombre
        FROM evaluacion_ficha_perfil efp
                 LEFT JOIN estado_evaluacion_ficha eef
                     ON eef.id = (SELECT x.id
                                  FROM estado_evaluacion_ficha x
                                  WHERE x.evaluacion_ficha_perfil_id = efp.id
                                  ORDER BY x.fecha_actualizacion DESC
                                  LIMIT 1)
                 LEFT JOIN estado_evaluacion ee ON ee.id = eef.estado_evaluacion_id
        """)
@Synchronize({"evaluacion_ficha_perfil", "estado_evaluacion_ficha", "estado_evaluacion"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionFichaPerfilJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "ficha_perfil_id", columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "representante_comite_id", columnDefinition = "uuid")
    private UUID representanteComiteId;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "estado_evaluacion_id")
    private String estadoEvaluacionId;

    @Column(name = "estado_evaluacion_nombre")
    private String estadoEvaluacionNombre;
}
