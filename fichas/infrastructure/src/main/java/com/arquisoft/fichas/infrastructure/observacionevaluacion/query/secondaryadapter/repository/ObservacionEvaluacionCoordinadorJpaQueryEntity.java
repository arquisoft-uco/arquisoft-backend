package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.secondaryadapter.repository;

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
        SELECT oe.id                         AS id,
               oe.evaluacion_ficha_perfil_id AS evaluacion_ficha_perfil_id,
               ev.ficha_perfil_id            AS ficha_perfil_id,
               ev.fecha_creacion             AS fecha_evaluacion,
               oe.observacion                AS observacion
        FROM observacion_evaluacion oe
                 JOIN evaluacion_ficha_perfil ev ON ev.id = oe.evaluacion_ficha_perfil_id
        """)
@Synchronize({"observacion_evaluacion", "evaluacion_ficha_perfil"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservacionEvaluacionCoordinadorJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evaluacion_ficha_perfil_id", columnDefinition = "uuid")
    private UUID evaluacionFichaPerfilId;

    @Column(name = "ficha_perfil_id", columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "fecha_evaluacion")
    private Instant fechaEvaluacion;

    @Column(name = "observacion")
    private String observacion;
}
