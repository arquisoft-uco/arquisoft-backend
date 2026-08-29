package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilUseCaseImpl implements ConsultarFichasPerfilUseCase {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<FichaPerfilReadModel> ejecutar(FichaPerfilCriteria entrada) {
        logger.debug(Mensajes.obtener(FichaPerfilKey.LOG_CONSULTANDO),
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = fichaPerfilQueryOutputPort.consultarTodas(entrada);

        logger.debug(Mensajes.obtener(FichaPerfilKey.LOG_CONSULTA_COMPLETADA),
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
