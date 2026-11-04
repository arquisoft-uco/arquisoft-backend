package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT r.id                 AS id,
               r.nombre_repositorio AS nombre_repositorio,
               r.descripcion        AS descripcion,
               a.id                 AS administrador_id,
               a.identificador      AS administrador_identificador,
               a.nombre             AS administrador_nombre,
               a.email              AS administrador_email
        FROM repositorio_artefacto r
                 JOIN administrador a ON a.id = r.administrador_id
        """)
@Synchronize({"repositorio_artefacto", "administrador"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepositorioArtefactoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "nombre_repositorio")
    private String nombreRepositorio;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "administrador_id", columnDefinition = "uuid")
    private UUID administradorId;

    @Column(name = "administrador_identificador")
    private String administradorIdentificador;

    @Column(name = "administrador_nombre")
    private String administradorNombre;

    @Column(name = "administrador_email")
    private String administradorEmail;
}
