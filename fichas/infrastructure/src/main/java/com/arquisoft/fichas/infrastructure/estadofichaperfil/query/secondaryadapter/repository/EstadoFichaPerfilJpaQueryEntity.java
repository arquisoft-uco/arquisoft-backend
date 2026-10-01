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
               vinc.estudiante_id      AS estudiante_id,
               efp.estado_ficha_id     AS estado_id,
               ec.nombre               AS estado_nombre,
               efp.fecha_actualizacion AS fecha_actualizacion
        FROM estado_ficha_perfil efp
                 JOIN estudiante_ficha_perfil vinc ON vinc.ficha_perfil_id = efp.ficha_perfil_id
                 JOIN estado_ficha ec ON ec.id = efp.estado_ficha_id
        """)
@Synchronize({"estado_ficha_perfil", "estudiante_ficha_perfil", "estado_ficha"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoFichaPerfilJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "ficha_perfil_id", columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "estudiante_id", columnDefinition = "uuid")
    private UUID estudianteId;

    @Column(name = "estado_id")
    private String estadoId;

    @Column(name = "estado_nombre")
    private String estadoNombre;

    @Column(name = "fecha_actualizacion")
    private Instant fechaActualizacion;
}
