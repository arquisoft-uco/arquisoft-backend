package com.arquisoft.entregables.infrastructure.proyectogrado.query.secondaryadapter.repository;

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
        SELECT epg.id                AS id,
               epg.estudiante_id     AS estudiante_id,
               epg.proyecto_grado_id AS proyecto_grado_id
        FROM estudiante_proyecto_grado epg
        WHERE epg.eliminado_en IS NULL
        """)
@Synchronize("estudiante_proyecto_grado")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteProyectoGradoAccesoJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;

    @Column(name = "proyecto_grado_id", columnDefinition = "uuid")
    private UUID proyectoGradoId;
}
