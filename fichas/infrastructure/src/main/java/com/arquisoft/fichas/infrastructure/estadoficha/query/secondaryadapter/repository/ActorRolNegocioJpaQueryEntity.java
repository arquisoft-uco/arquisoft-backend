package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.security.FichasRoles;
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
import java.util.UUID;

@Entity
@Immutable
@Subselect("SELECT a.id AS actor_id, '" + FichasRoles.Negocio.ASESOR_FICHA + "' AS rol "
        + "FROM asesor_ficha a WHERE a.eliminado_en IS NULL "
        + "UNION ALL "
        + "SELECT r.id AS actor_id, '" + FichasRoles.Negocio.REPRESENTANTE_COMITE + "' AS rol "
        + "FROM representante_comite_curriculum r WHERE r.eliminado_en IS NULL")
@Synchronize({"asesor_ficha", "representante_comite_curriculum"})
@IdClass(ActorRolNegocioJpaQueryEntity.Clave.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActorRolNegocioJpaQueryEntity {

    @Id
    @Column(name = "actor_id")
    private UUID actorId;

    @Id
    @Column(name = "rol")
    private String rol;

    public record Clave(UUID actorId, String rol) implements Serializable {
    }
}
