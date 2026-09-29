package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.secondaryadapter.repository;

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

@Entity
@Immutable
@Subselect("""
        SELECT t.id          AS id,
               t.nombre      AS nombre,
               t.descripcion AS descripcion
        FROM tipo_solicitud t
        """)
@Synchronize("tipo_solicitud")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoSolicitudJpaQueryEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;
}
