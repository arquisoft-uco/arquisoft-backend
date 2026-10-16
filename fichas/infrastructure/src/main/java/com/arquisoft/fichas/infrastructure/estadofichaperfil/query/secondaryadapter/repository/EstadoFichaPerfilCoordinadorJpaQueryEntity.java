package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

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
        SELECT efp.id                  AS id,
               efp.ficha_perfil_id     AS ficha_perfil_id,
               efp.estado_ficha_id     AS estado_id,
               ef.nombre               AS estado_nombre,
               efp.fecha_actualizacion AS fecha_actualizacion
        FROM estado_ficha_perfil efp
                 JOIN estado_ficha ef ON ef.id = efp.estado_ficha_id
        """)
@Synchronize({"estado_ficha_perfil", "estado_ficha"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoFichaPerfilCoordinadorJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "ficha_perfil_id", columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;

    @Column(name = "fecha_actualizacion")
    private Instant fechaActualizacion;
}
