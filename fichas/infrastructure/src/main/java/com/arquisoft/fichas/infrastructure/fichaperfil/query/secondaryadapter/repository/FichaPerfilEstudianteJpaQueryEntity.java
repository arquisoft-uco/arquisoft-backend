package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

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
        SELECT f.id                  AS id,
               f.titulo_proyecto     AS titulo_proyecto,
               a.id                  AS asesor_id,
               a.identificador       AS asesor_identificador,
               a.nombre              AS asesor_nombre,
               a.email               AS asesor_email,
               ef.estado_ficha_id    AS estado_id,
               ec.nombre             AS estado_nombre,
               ef.fecha_actualizacion AS estado_fecha_actualizacion
        FROM ficha_perfil f
                 JOIN asesor_ficha a ON a.id = f.asesor_ficha_id
                 JOIN LATERAL (SELECT e.estado_ficha_id, e.fecha_actualizacion
                               FROM estado_ficha_perfil e
                               WHERE e.ficha_perfil_id = f.id
                               ORDER BY e.fecha_actualizacion DESC
                               LIMIT 1) ef ON TRUE
                 JOIN estado_ficha ec ON ec.id = ef.estado_ficha_id
        """)
@Synchronize({"ficha_perfil", "asesor_ficha", "estado_ficha_perfil", "estado_ficha"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FichaPerfilEstudianteJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;

    @Column(name = "asesor_id", columnDefinition = "uuid")
    private UUID asesorId;

    @Column(name = "asesor_identificador")
    private String asesorIdentificador;

    @Column(name = "asesor_nombre")
    private String asesorNombre;

    @Column(name = "asesor_email")
    private String asesorEmail;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;

    @Column(name = "estado_fecha_actualizacion")
    private Instant estadoFechaActualizacion;
}
