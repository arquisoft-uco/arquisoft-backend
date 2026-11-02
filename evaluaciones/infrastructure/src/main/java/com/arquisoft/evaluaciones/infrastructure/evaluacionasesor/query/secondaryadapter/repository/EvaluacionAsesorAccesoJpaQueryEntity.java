package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

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
        SELECT ea.id AS id, ea.asesor_id AS asesor_id, ea.evaluacion_id AS evaluacion_id
        FROM evaluacion_asesor ea
        """)
@Synchronize("evaluacion_asesor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAsesorAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "asesor_id")
    private UUID asesorId;

    @Column(name = "evaluacion_id")
    private UUID evaluacionId;
}
