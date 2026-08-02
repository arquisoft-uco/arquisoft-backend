package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilUseCaseImpl implements ConsultarFichasPerfilUseCase {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public PaginatedResult<FichaPerfilReadModel> ejecutar(FichaPerfilCriteria entrada) {
        logger.debug(catalog.obtener(FichasKeys.FichaPerfil.LOG_CONSULTANDO), entrada.getPagina(), entrada.getTamanio());

        PaginatedResult<FichaPerfilReadModel> resultado = fichaPerfilQueryOutputPort.consultarTodas(entrada);

        logger.info(catalog.obtener(FichasKeys.FichaPerfil.LOG_CONSULTA_COMPLETADA),
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
