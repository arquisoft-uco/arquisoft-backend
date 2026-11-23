package com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.secondaryadapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "coordinador_proyecto_grado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinadorProyectoGradoJpaEntity {

    @Id
    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGrado;

    @Column(name = "coordinador_id", nullable = false, columnDefinition = "uuid")
    private UUID coordinador;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;
}
