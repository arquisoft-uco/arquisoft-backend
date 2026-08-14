package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.FichaPerfilJpaQueryEntity;

public final class FichaPerfilQueryMapper {

    private FichaPerfilQueryMapper() {}

    public static FichaPerfilReadModel toReadModel(FichaPerfilJpaQueryEntity entity) {
        return new FichaPerfilReadModel(
                entity.getId(),
                entity.getTituloProyecto(),
                new AsesorFichaReadModel(
                        entity.getAsesorId(),
                        entity.getAsesorIdentificador(),
                        entity.getAsesorNombre(),
                        entity.getAsesorEmail()));
    }
}
