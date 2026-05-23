package com.arquisoft.fichas.infrastructure.adapter.out.persistence.fichaperfil;

import com.arquisoft.fichas.domain.model.AsesorFicha;
import com.arquisoft.fichas.domain.model.FichaPerfilAggregate;
import com.arquisoft.fichas.infrastructure.adapter.out.persistence.asesorficha.AsesorFichaJpaEntity;

/**
 * Mapper entre la capa de persistencia y el dominio del contexto fichas.
 * Centraliza toda la lógica de traducción, manteniendo las JPA entities
 * como objetos de datos puros y el adapter libre de detalles de construcción.
 */
public final class FichaPerfilMapper {

    private FichaPerfilMapper() {}

    public static FichaPerfilAggregate toDomain(FichaPerfilJpaEntity entity) {
        return FichaPerfilAggregate.rebuild(
                entity.getId(),
                entity.getTituloProyecto(),
                AsesorFicha.rebuild(
                        entity.getAsesorFicha().getId(),
                        entity.getAsesorFicha().getIdentificador(),
                        entity.getAsesorFicha().getNombre(),
                        entity.getAsesorFicha().getEmail()
                )
        );
    }

    public static FichaPerfilJpaEntity toEntity(FichaPerfilAggregate domain) {
        return FichaPerfilJpaEntity.builder()
                .id(domain.getId())
                .tituloProyecto(domain.getTituloProyecto())
                .asesorFicha(AsesorFichaJpaEntity.builder()
                        .id(domain.getAsesorFicha().getId())
                        .identificador(domain.getAsesorFicha().getIdentificador())
                        .nombre(domain.getAsesorFicha().getNombre())
                        .email(domain.getAsesorFicha().getEmail())
                        .build())
                .build();
    }
}
