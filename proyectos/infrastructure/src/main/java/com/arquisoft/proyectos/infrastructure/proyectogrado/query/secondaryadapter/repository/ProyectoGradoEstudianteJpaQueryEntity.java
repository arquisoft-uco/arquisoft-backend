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
        SELECT epg.id                 AS id,
               epg.proyecto_grado_id  AS proyecto_grado_id,
               epg.estudiante_id      AS estudiante_id,
               pg.ficha_perfil_id     AS ficha_perfil_id,
               pg.titulo_proyecto     AS titulo_proyecto,
               esp.estado_proyecto_id AS estado_id,
               ep.nombre              AS estado_nombre
        FROM estudiante_proyecto_grado epg
        JOIN estudiante e ON e.id = epg.estudiante_id AND e.eliminado_en IS NULL
        JOIN proyecto_grado pg ON pg.id = epg.proyecto_grado_id
        JOIN estado_proyecto_grado esp ON esp.proyecto_grado_id = pg.id
             AND NOT EXISTS (SELECT 1 FROM estado_proyecto_grado s
                             WHERE s.proyecto_grado_id = esp.proyecto_grado_id
                               AND s.fecha_actualizacion > esp.fecha_actualizacion)
        JOIN estado_proyecto ep ON ep.id = esp.estado_proyecto_id
        """)
@Synchronize({"estudiante_proyecto_grado", "estudiante", "proyecto_grado", "estado_proyecto_grado", "estado_proyecto"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoGradoEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "estudiante_id")
    private UUID estudianteId;

    @Column(name = "ficha_perfil_id")
    private UUID fichaPerfilId;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;
}
