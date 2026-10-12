package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository;

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
        SELECT r.estado_ficha_id AS estado_ficha_id,
               r.rol             AS rol
        FROM estado_ficha_rol r
        """)
@Synchronize("estado_ficha_rol")
@IdClass(EstadoFichaRolJpaQueryEntity.Clave.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoFichaRolJpaQueryEntity {

    @Id
    @Column(name = "estado_ficha_id")
    private String estadoFichaId;

    @Id
    @Column(name = "rol")
    private String rol;

    public record Clave(String estadoFichaId, String rol) implements Serializable {
    }
}
