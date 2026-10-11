package com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_observacion_revision")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoObservacionRevisionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 60)
    private String id;

    @Column(name = "nombre", nullable = false, unique = true, length = 60)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 300)
    private String descripcion;
}
