package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionParticipanteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.FichaPerfilRepresentanteJpaQueryEntity;

public final class FichaPerfilRepresentanteQueryMapper {

    private FichaPerfilRepresentanteQueryMapper() {}

    public static FichaPerfilRepresentanteReadModel toReadModel(FichaPerfilRepresentanteJpaQueryEntity entity) {
        var ficha = new FichaPerfilReadModel(
                entity.getFichaId(),
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

        return new FichaPerfilRepresentanteReadModel(
                ficha,
                new EvaluacionParticipanteReadModel(
                        entity.getId(),
                        entity.getEvaluacionEstadoId(),
                        entity.getEvaluacionEstadoNombre()));
    }
}
