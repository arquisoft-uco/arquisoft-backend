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

import java.util.UUID;

@Entity
@Immutable
@Subselect("""
        SELECT e.id                      AS id,
               e.ficha_perfil_id         AS ficha_perfil_id,
               e.representante_comite_id AS representante_comite_id
        FROM evaluacion_ficha_perfil e
        """)
@Synchronize({"evaluacion_ficha_perfil"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionRepresentanteFichaJpaQueryEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "ficha_perfil_id", columnDefinition = "uuid")
    private UUID fichaPerfilId;

    @Column(name = "representante_comite_id", columnDefinition = "uuid")
    private UUID representanteComiteId;
}
