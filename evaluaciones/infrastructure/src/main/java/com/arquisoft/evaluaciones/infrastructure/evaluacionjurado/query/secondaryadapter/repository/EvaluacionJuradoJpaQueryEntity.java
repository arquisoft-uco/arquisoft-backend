package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

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
        SELECT ej.id             AS id,
               ej.evaluacion_id  AS evaluacion_id,
               j.id              AS jurado_id,
               j.nombre          AS jurado_nombre,
               j.email           AS jurado_email
        FROM evaluacion_jurado ej
        JOIN jurado j ON j.id = ej.jurado_id
        """)
@Synchronize({"evaluacion_jurado", "jurado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionJuradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_id")
    private UUID evaluacionId;

    @Column(name = "jurado_id")
    private UUID juradoId;

    @Column(name = "jurado_nombre")
    private String juradoNombre;

    @Column(name = "jurado_email")
    private String juradoEmail;
}
