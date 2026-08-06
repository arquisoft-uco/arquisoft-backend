package com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_evaluacion")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoEvaluacionEntity {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;
}
