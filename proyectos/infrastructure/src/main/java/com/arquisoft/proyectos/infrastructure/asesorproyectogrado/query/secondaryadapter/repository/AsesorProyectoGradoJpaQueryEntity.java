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

import java.time.Instant;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT apg.id                         AS id,
               apg.proyecto_grado_id          AS proyecto_grado_id,
               apg.asesor_id                  AS asesor_id,
               a.identificador                AS identificador,
               a.nombre                       AS nombre,
               a.email                        AS email,
               apg.estado_asesor_id           AS estado_asesor,
               ea.nombre                      AS estado_asesor_nombre,
               apg.fecha_asignado             AS fecha_asignado,
               (a.eliminado_en IS NULL)       AS vigente,
               NOT EXISTS (SELECT 1 FROM asesor_proyecto_grado s
                           WHERE s.asesor_id = apg.asesor_id
                             AND s.proyecto_grado_id = apg.proyecto_grado_id
                             AND s.fecha_asignado > apg.fecha_asignado) AS es_actual
        FROM asesor_proyecto_grado apg
        JOIN asesor a ON a.id = apg.asesor_id
        JOIN estado_asesor ea ON ea.id = apg.estado_asesor_id
        """)
@Synchronize({"asesor_proyecto_grado", "asesor", "estado_asesor"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsesorProyectoGradoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "proyecto_grado_id")
    private UUID proyectoGradoId;

    @Column(name = "asesor_id")
    private UUID asesorId;

    @Column(name = "identificador")
    private String identificador;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "estado_asesor")
    private String estadoAsesor;

    @Column(name = "estado_asesor_nombre")
    private String estadoAsesorNombre;

    @Column(name = "fecha_asignado")
    private Instant fechaAsignado;

    @Column(name = "vigente")
    private boolean vigente;

    @Column(name = "es_actual")
    private boolean actual;
}
