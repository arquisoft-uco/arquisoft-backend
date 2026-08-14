package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.FichaPerfilJpaQueryEntity;

public final class FichaPerfilQueryMapper {

    private FichaPerfilQueryMapper() {}

    public static FichaPerfilReadModel toReadModel(FichaPerfilJpaQueryEntity entity) {
        return FichaPerfilReadModel.builder()
                .id(entity.getId())
                .tituloProyecto(entity.getTituloProyecto())
                .asesorFicha(AsesorFichaReadModel.builder()
                        .id(entity.getAsesorId())
                        .identificador(entity.getAsesorIdentificador())
                        .nombre(entity.getAsesorNombre())
                        .email(entity.getAsesorEmail())
                        .build())
                .build();
    }
}
