package com.arquisoft.fichas.infrastructure.fichaperfil.persistence;

import com.arquisoft.fichas.application.asesorficha.query.AsesorFichaReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaEntity;

public final class FichaPerfilMapper {

    private FichaPerfilMapper() {}

    public static FichaPerfilAggregate toDomain(FichaPerfilEntity entity) {
        return FichaPerfilAggregate.reconstruir(
                entity.getId(),
                entity.getTituloProyecto(),
                entity.getAsesorFicha().getId()
        );
    }

    public static FichaPerfilReadModel toReadModel(FichaPerfilEntity entity) {
        return FichaPerfilReadModel.builder()
                .id(entity.getId())
                .tituloProyecto(entity.getTituloProyecto())
                .asesorFicha(AsesorFichaReadModel.builder()
                        .id(entity.getAsesorFicha().getId())
                        .identificador(entity.getAsesorFicha().getIdentificador())
                        .nombre(entity.getAsesorFicha().getNombre())
                        .email(entity.getAsesorFicha().getEmail())
                        .build())
                .build();
    }

    public static FichaPerfilEntity toEntity(FichaPerfilAggregate domain, AsesorFichaEntity asesorRef) {
        return FichaPerfilEntity.builder()
                .id(domain.getId())
                .tituloProyecto(domain.getTituloProyecto())
                .asesorFicha(asesorRef)
                .build();
    }
}
