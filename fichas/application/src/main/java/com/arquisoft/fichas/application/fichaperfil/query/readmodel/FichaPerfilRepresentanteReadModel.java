package com.arquisoft.fichas.application.fichaperfil.query.readmodel;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionParticipanteReadModel;

public record FichaPerfilRepresentanteReadModel(
        FichaPerfilReadModel ficha,
        EvaluacionParticipanteReadModel evaluacion
) {
}
