package com.arquisoft.fichas.application.fichaperfil.query.mapper;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;

public final class FichaPerfilQueryMapper {

    private FichaPerfilQueryMapper() {}

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
}
