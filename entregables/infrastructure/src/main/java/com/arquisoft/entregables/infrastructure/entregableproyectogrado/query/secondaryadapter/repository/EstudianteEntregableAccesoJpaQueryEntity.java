package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT epg.id            AS id,
               e.id              AS entregable_proyecto_grado_id,
               epg.estudiante_id AS estudiante_id
        FROM entregable_proyecto_grado e
        JOIN estudiante_proyecto_grado epg ON epg.proyecto_grado_id = e.proyecto_grado_id
        WHERE epg.eliminado_en IS NULL
        """)
@Synchronize({"entregable_proyecto_grado", "estudiante_proyecto_grado"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteEntregableAccesoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "entregable_proyecto_grado_id")
    private UUID entregableProyectoGradoId;

    @Column(name = "estudiante_id")
    private UUID estudianteId;
}
