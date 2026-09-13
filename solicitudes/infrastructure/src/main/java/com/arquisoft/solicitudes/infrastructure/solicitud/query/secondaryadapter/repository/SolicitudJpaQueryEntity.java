package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository;

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
        SELECT s.id                AS id,
               s.mensaje_solicitud AS mensaje_solicitud,
               s.fecha_creacion    AS fecha_creacion,
               ts.id               AS tipo_solicitud_id,
               ts.nombre           AS tipo_solicitud_nombre,
               d.usuario_id        AS destinatario_usuario_id,
               ru.id               AS remitente_usuario_id,
               ru.identificador    AS remitente_identificador,
               ru.nombre           AS remitente_nombre,
               ru.email            AS remitente_email
        FROM solicitud s
                 JOIN destinatario d    ON d.id  = s.destinatario_id
                 JOIN remitente r       ON r.id  = s.remitente_id
                 JOIN usuario ru        ON ru.id = r.usuario_id
                 JOIN tipo_solicitud ts ON ts.id = s.tipo_solicitud_id
        """)
@Synchronize({"solicitud", "destinatario", "remitente", "usuario", "tipo_solicitud"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "mensaje_solicitud")
    private String mensajeSolicitud;

    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "tipo_solicitud_id")
    private String tipoSolicitudId;

    @Column(name = "tipo_solicitud_nombre")
    private String tipoSolicitudNombre;

    @Column(name = "destinatario_usuario_id", columnDefinition = "uuid")
    private UUID destinatarioUsuarioId;

    @Column(name = "remitente_usuario_id", columnDefinition = "uuid")
    private UUID remitenteUsuarioId;

    @Column(name = "remitente_identificador")
    private String remitenteIdentificador;

    @Column(name = "remitente_nombre")
    private String remitenteNombre;

    @Column(name = "remitente_email")
    private String remitenteEmail;
}
