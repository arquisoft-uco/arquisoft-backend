package com.arquisoft.fichas.application.fichaperfil.query.secondaryport;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface FichaPerfilRepresentanteQueryOutputPort {

    PaginatedResult<FichaPerfilRepresentanteReadModel> consultarParticipacionRepresentante(
            FichaPerfilRepresentanteCriteria criteria);
}
