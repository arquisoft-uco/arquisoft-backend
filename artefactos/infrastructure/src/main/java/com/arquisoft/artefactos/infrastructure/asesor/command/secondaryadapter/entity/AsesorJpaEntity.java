package com.arquisoft.artefactos.infrastructure.asesor.command.secondaryadapter.entity;

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
@Table(name = "asesor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesorJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "email", nullable = false, length = 50)
    private String email;
}
