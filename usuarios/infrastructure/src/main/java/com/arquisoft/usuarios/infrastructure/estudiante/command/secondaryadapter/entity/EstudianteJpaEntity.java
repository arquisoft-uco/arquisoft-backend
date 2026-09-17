package com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.entity;

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
@Table(name = "estudiante")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteJpaEntity {

    @Id
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "eliminado_en")
    private Instant eliminadoEn;
}
