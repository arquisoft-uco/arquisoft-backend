package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.io.Serializable;

@Entity
@Immutable
@Subselect("""
        SELECT r.tipo_solicitud_id AS tipo_solicitud_id,
               r.rol               AS rol
        FROM tipo_solicitud_rol r
        """)
@Synchronize("tipo_solicitud_rol")
@IdClass(TipoSolicitudRolJpaQueryEntity.Clave.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TipoSolicitudRolJpaQueryEntity {

    @Id
    @Column(name = "tipo_solicitud_id")
    private String tipoSolicitudId;

    @Id
    @Column(name = "rol")
    private String rol;

    public record Clave(String tipoSolicitudId, String rol) implements Serializable {
    }
}
