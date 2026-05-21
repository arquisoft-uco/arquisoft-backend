package com.arquisoft.fichas.infrastructure.adapter.out.persistence.fichaperfil;

import com.arquisoft.fichas.domain.model.AsesorFicha;
import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.infrastructure.adapter.out.persistence.asesorficha.AsesorFichaJpaEntity;

/**
 * Mapper entre la capa de persistencia y el dominio del contexto fichas.
 * Centraliza toda la lógica de traducción, manteniendo las JPA entities
 * como objetos de datos puros y el adapter libre de detalles de construcción.
 */
public final class FichaPerfilMapper {

    private FichaPerfilMapper() {}

    public static FichaPerfil toDomain(FichaPerfilJpaEntity entity) {
        return FichaPerfil.rebuild(
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

    public static FichaPerfilJpaEntity toEntity(FichaPerfil domain) {
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
