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
        SELECT pg.id                  AS id,
               pg.ficha_perfil_id     AS ficha_perfil_id,
               pg.titulo_proyecto     AS titulo_proyecto,
               pg.coordinador_id      AS coordinador_id,
               epg.estado_proyecto_id AS estado_id,
               e.nombre               AS estado_nombre
        FROM proyecto_grado pg
        JOIN estado_proyecto_grado epg ON epg.proyecto_grado_id = pg.id
             AND NOT EXISTS (SELECT 1 FROM estado_proyecto_grado s
                             WHERE s.proyecto_grado_id = epg.proyecto_grado_id
                               AND s.fecha_actualizacion > epg.fecha_actualizacion)
        JOIN estado_proyecto e ON e.id = epg.estado_proyecto_id
        """)
@Synchronize({"proyecto_grado", "estado_proyecto_grado", "estado_proyecto"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoGradoListadoJpaQueryEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "ficha_perfil_id")
    private UUID fichaPerfilId;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "coordinador_id")
    private UUID coordinadorId;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;
}
