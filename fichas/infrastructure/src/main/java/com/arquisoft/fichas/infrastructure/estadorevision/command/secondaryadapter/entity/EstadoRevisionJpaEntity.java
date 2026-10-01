package com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_revision")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoRevisionJpaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "nombre", nullable = false, unique = true, length = 60)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 300)
    private String descripcion;
}
