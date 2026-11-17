package com.arquisoft.biblioteca.infrastructure.proyectogrado.command.secondaryadapter.entity;

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
@Table(name = "proyecto_grado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoGradoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;
}
