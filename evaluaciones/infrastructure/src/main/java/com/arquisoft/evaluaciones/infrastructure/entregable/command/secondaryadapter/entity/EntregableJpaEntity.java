package com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "entregable")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregableJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "proyecto", nullable = false, length = 200)
    private String proyecto;

    @Column(name = "version_entregable", nullable = false)
    private Integer version;

    @Column(name = "proyecto_grado_id", nullable = false, columnDefinition = "uuid")
    private UUID proyectoGrado;
}
