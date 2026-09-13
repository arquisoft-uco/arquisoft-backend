package com.arquisoft.fichas.infrastructure.coordinador.command.secondaryadapter.entity;

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
@Table(name = "coordinador")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinadorJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "identificador", nullable = false, length = 30)
    private String identificador;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;
}
