package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.FichaPerfilDisponibleEvaluacionJpaQueryEntity;

public final class FichaPerfilDisponibleEvaluacionQueryMapper {

    private FichaPerfilDisponibleEvaluacionQueryMapper() {}

    public static FichaPerfilReadModel toReadModel(FichaPerfilDisponibleEvaluacionJpaQueryEntity entity) {
        return new FichaPerfilReadModel(
                entity.getId(),
                entity.getTituloProyecto(),
                new AsesorFichaReadModel(
                        entity.getAsesorId(),
                        entity.getAsesorIdentificador(),
                        entity.getAsesorNombre(),
                        entity.getAsesorEmail()),
                new EstadoFichaPerfilReadModel(
                        entity.getEstadoId(),
                        entity.getEstadoNombre(),
                        entity.getEstadoFechaActualizacion()));
    }
}
