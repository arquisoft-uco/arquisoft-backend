package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

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
        SELECT ea.id                         AS id,
               ea.artefacto_id               AS artefacto_id,
               ea.estudiante_id              AS estudiante_id,
               e.identificador               AS identificador,
               e.nombre                      AS nombre,
               e.email                       AS email,
               (e.eliminado_en IS NULL)      AS vigente
        FROM estudiante_artefacto ea
                 JOIN estudiante e ON e.id = ea.estudiante_id
        """)
@Synchronize({"estudiante_artefacto", "estudiante"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteArtefactoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "artefacto_id", columnDefinition = "uuid")
    private UUID artefactoId;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;

    @Column(name = "identificador")
    private String identificador;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "vigente")
    private boolean vigente;
}
