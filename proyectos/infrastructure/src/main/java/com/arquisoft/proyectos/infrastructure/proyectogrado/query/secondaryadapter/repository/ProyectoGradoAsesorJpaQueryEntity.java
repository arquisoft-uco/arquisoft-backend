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
        SELECT apg.id                 AS id,
               apg.proyecto_grado_id  AS proyecto_grado_id,
               apg.asesor_id          AS asesor_id,
               apg.estado_asesor_id   AS estado_asesor_id,
               pg.ficha_perfil_id     AS ficha_perfil_id,
               pg.titulo_proyecto     AS titulo_proyecto,
               esp.estado_proyecto_id AS estado_id,
               ep.nombre              AS estado_nombre
        FROM asesor_proyecto_grado apg
        JOIN asesor a ON a.id = apg.asesor_id AND a.eliminado_en IS NULL
        JOIN proyecto_grado pg ON pg.id = apg.proyecto_grado_id
        JOIN estado_proyecto_grado esp ON esp.proyecto_grado_id = pg.id
             AND NOT EXISTS (SELECT 1 FROM estado_proyecto_grado s
                             WHERE s.proyecto_grado_id = esp.proyecto_grado_id
                               AND s.fecha_actualizacion > esp.fecha_actualizacion)
        JOIN estado_proyecto ep ON ep.id = esp.estado_proyecto_id
        WHERE NOT EXISTS (SELECT 1 FROM asesor_proyecto_grado s
                          WHERE s.asesor_id = apg.asesor_id
                            AND s.proyecto_grado_id = apg.proyecto_grado_id
                            AND s.fecha_asignado > apg.fecha_asignado)
        """)
@Synchronize({"asesor_proyecto_grado", "asesor", "proyecto_grado", "estado_proyecto_grado", "estado_proyecto"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoGradoAsesorJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "asesor_id")
    private UUID asesorId;

    @Column(name = "estado_asesor_id")
    private String estadoAsesorId;

    @Column(name = "ficha_perfil_id")
    private UUID fichaPerfilId;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;
}
