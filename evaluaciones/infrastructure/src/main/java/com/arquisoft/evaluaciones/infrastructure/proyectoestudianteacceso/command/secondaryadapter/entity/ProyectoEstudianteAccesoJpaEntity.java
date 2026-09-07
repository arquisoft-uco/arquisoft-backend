package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "proyecto_estudiante_acceso")
@IdClass(ProyectoEstudianteAccesoId.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoEstudianteAccesoJpaEntity {

    @Id
    @Column(name = "proyecto_id")
    private UUID proyecto;

    @Id
    @Column(name = "estudiante_id")
    private UUID estudiante;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;
}
