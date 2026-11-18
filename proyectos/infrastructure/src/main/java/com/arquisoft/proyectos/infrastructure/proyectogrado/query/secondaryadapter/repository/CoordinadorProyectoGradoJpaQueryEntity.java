package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

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
               epg.proyecto_grado_id AS proyecto_grado_id,
               pg.titulo_proyecto    AS titulo_proyecto,
               pg.coordinador_id     AS coordinador_id,
               c.identificador       AS identificador,
               c.nombre              AS nombre,
               c.email               AS email
        FROM estudiante_proyecto_grado epg
        JOIN estudiante e ON e.id = epg.estudiante_id AND e.eliminado_en IS NULL
        JOIN proyecto_grado pg ON pg.id = epg.proyecto_grado_id
        JOIN coordinador c ON c.id = pg.coordinador_id AND c.eliminado_en IS NULL
        """)
@Synchronize({"estudiante_proyecto_grado", "estudiante", "proyecto_grado", "coordinador"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinadorProyectoGradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "estudiante_id")
    private UUID estudianteId;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "coordinador_id")
    private UUID coordinadorId;

    @Column(name = "identificador")
    private String identificador;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;
}
