package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.secondaryadapter.repository;

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
        SELECT epg.id                         AS id,
               epg.proyecto_grado_id          AS proyecto_grado_id,
               epg.estudiante_id              AS estudiante_id,
               e.identificador                AS identificador,
               e.nombre                       AS nombre,
               e.email                        AS email,
               (e.eliminado_en IS NULL)       AS vigente
        FROM estudiante_proyecto_grado epg
        JOIN estudiante e ON e.id = epg.estudiante_id
        """)
@Synchronize({"estudiante_proyecto_grado", "estudiante"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteProyectoGradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "estudiante_id")
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
