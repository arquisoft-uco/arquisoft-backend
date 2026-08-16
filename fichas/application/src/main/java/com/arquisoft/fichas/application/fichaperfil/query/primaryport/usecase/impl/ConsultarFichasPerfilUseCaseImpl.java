package com.arquisoft.fichas.application.fichaperfil.query.primaryport.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.usecase.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
public class ConsultarFichasPerfilUseCaseImpl implements ConsultarFichasPerfilUseCase {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public PaginatedResult<FichaPerfilReadModel> ejecutar(FichaPerfilCriteria entrada) {
        logger.debug(catalogo.obtener(FichaPerfilKey.LOG_CONSULTANDO), entrada.getPagina(), entrada.getTamanio());

        PaginatedResult<FichaPerfilReadModel> resultado = fichaPerfilQueryOutputPort.consultarTodas(entrada);

        logger.info(catalogo.obtener(FichaPerfilKey.LOG_CONSULTA_COMPLETADA),
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
