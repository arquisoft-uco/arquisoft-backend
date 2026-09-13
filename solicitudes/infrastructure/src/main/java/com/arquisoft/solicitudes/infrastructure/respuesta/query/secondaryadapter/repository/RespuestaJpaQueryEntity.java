package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository;

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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT r.id                AS id,
               r.contenido         AS contenido,
               r.fecha_respuesta   AS fecha_respuesta,
               er.id               AS estado_respuesta_id,
               er.nombre           AS estado_respuesta_nombre,
               s.id                AS solicitud_id,
               s.mensaje_solicitud AS mensaje_solicitud,
               s.fecha_creacion    AS fecha_creacion,
               ts.id               AS tipo_solicitud_id,
               ts.nombre           AS tipo_solicitud_nombre,
               d.usuario_id        AS destinatario_usuario_id,
               du.identificador    AS destinatario_identificador,
               du.nombre           AS destinatario_nombre,
               du.email            AS destinatario_email,
               ru.id               AS remitente_usuario_id,
               ru.identificador    AS remitente_identificador,
               ru.nombre           AS remitente_nombre,
               ru.email            AS remitente_email
        FROM respuesta r
                 JOIN estado_respuesta er ON er.id  = r.estado_respuesta_id
                 JOIN solicitud s         ON s.id   = r.solicitud_id
                 JOIN destinatario d      ON d.id   = s.destinatario_id
                 JOIN usuario du          ON du.id  = d.usuario_id
                 JOIN remitente rem       ON rem.id = s.remitente_id
                 JOIN usuario ru          ON ru.id  = rem.usuario_id
                 JOIN tipo_solicitud ts   ON ts.id  = s.tipo_solicitud_id
        """)
@Synchronize({"respuesta", "estado_respuesta", "solicitud", "destinatario", "remitente",
        "usuario", "tipo_solicitud"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "contenido")
    private String contenido;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "estado_respuesta_id")
    private String estadoRespuestaId;

    @Column(name = "estado_respuesta_nombre")
    private String estadoRespuestaNombre;

    @Column(name = "solicitud_id", columnDefinition = "uuid")
    private UUID solicitudId;

    @Column(name = "mensaje_solicitud")
    private String mensajeSolicitud;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "tipo_solicitud_id")
    private String tipoSolicitudId;

    @Column(name = "tipo_solicitud_nombre")
    private String tipoSolicitudNombre;

    @Column(name = "destinatario_usuario_id", columnDefinition = "uuid")
    private UUID destinatarioUsuarioId;

    @Column(name = "destinatario_identificador")
    private String destinatarioIdentificador;

    @Column(name = "destinatario_nombre")
    private String destinatarioNombre;

    @Column(name = "destinatario_email")
    private String destinatarioEmail;

    @Column(name = "remitente_usuario_id", columnDefinition = "uuid")
    private UUID remitenteUsuarioId;

    @Column(name = "remitente_identificador")
    private String remitenteIdentificador;

    @Column(name = "remitente_nombre")
    private String remitenteNombre;

    @Column(name = "remitente_email")
    private String remitenteEmail;
}
