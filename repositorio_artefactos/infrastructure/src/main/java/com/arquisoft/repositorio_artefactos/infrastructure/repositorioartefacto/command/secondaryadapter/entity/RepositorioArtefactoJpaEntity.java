package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.entity;

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
@Table(name = "repositorio_artefacto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepositorioArtefactoJpaEntity {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "nombre_repositorio", nullable = false, length = 100)
    private String nombreRepositorio;

    @Column(name = "descripcion", nullable = false, length = 300)
    private String descripcion;

    @Column(name = "administrador_id", nullable = false, columnDefinition = "UUID")
    private UUID administrador;
}
