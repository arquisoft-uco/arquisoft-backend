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

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT ea.id            AS id,
               ea.evaluacion_id AS evaluacion_id,
               a.id             AS asesor_id,
               a.nombre         AS asesor_nombre,
               a.email          AS asesor_email,
               ea.resumen_cualitativo  AS resumen_cualitativo,
               ea.resumen_cuantitativo AS resumen_cuantitativo,
               ea.nota_acumulada       AS nota_acumulada
        FROM evaluacion_asesor ea
        JOIN asesor a ON a.id = ea.asesor_id
        """)
@Synchronize({"evaluacion_asesor", "asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAsesorDeEvaluacionJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "evaluacion_id")
    private UUID evaluacionId;

    @Column(name = "asesor_id")
    private UUID asesorId;

    @Column(name = "asesor_nombre")
    private String asesorNombre;

    @Column(name = "asesor_email")
    private String asesorEmail;

    @Column(name = "resumen_cualitativo")
    private BigDecimal resumenCualitativo;

    @Column(name = "resumen_cuantitativo")
    private BigDecimal resumenCuantitativo;

    @Column(name = "nota_acumulada")
    private BigDecimal notaAcumulada;
}
