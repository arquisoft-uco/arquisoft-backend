package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilRepresentanteQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilRepresentanteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilRepresentanteUseCaseImpl
        implements ConsultarFichasPerfilRepresentanteUseCase {

    private final FichaPerfilRepresentanteQueryOutputPort fichaPerfilRepresentanteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<FichaPerfilRepresentanteReadModel> ejecutar(FichaPerfilRepresentanteCriteria entrada) {
        logger.debug(FichaPerfilKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = fichaPerfilRepresentanteQueryOutputPort.consultarParticipacionRepresentante(entrada);

        logger.debug(FichaPerfilKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
