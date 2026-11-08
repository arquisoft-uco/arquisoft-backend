package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

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
               apg.asesor_id         AS asesor_id,
               a.identificador       AS identificador,
               a.nombre              AS nombre,
               a.email               AS email
        FROM estudiante_proyecto_grado epg
        JOIN estudiante e ON e.id = epg.estudiante_id AND e.eliminado_en IS NULL
        JOIN proyecto_grado pg ON pg.id = epg.proyecto_grado_id
        JOIN asesor_proyecto_grado apg ON apg.proyecto_grado_id = epg.proyecto_grado_id
             AND apg.estado_asesor_id = 'ASIGNADO'
             AND NOT EXISTS (SELECT 1 FROM asesor_proyecto_grado s
                             WHERE s.asesor_id = apg.asesor_id
                               AND s.proyecto_grado_id = apg.proyecto_grado_id
                               AND s.fecha_asignado > apg.fecha_asignado)
        JOIN asesor a ON a.id = apg.asesor_id AND a.eliminado_en IS NULL
        WHERE NOT EXISTS (SELECT 1 FROM asesor_proyecto_grado o
                          JOIN asesor oa ON oa.id = o.asesor_id AND oa.eliminado_en IS NULL
                          WHERE o.proyecto_grado_id = apg.proyecto_grado_id
                            AND o.estado_asesor_id = 'ASIGNADO'
                            AND NOT EXISTS (SELECT 1 FROM asesor_proyecto_grado os
                                            WHERE os.asesor_id = o.asesor_id
                                              AND os.proyecto_grado_id = o.proyecto_grado_id
                                              AND os.fecha_asignado > o.fecha_asignado)
                            AND (o.fecha_asignado > apg.fecha_asignado
                                 OR (o.fecha_asignado = apg.fecha_asignado AND o.id > apg.id)))
        """)
@Synchronize({"estudiante_proyecto_grado", "estudiante", "proyecto_grado", "asesor_proyecto_grado", "asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesorActualProyectoGradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "estudiante_id")
    private UUID estudianteId;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "asesor_id")
    private UUID asesorId;

    @Column(name = "identificador")
    private String identificador;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;
}
