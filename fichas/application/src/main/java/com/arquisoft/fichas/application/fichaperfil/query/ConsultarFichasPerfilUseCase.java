package com.arquisoft.fichas.application.fichaperfil.query;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.in.ConsultarFichasPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilUseCase implements ConsultarFichasPerfilInputPort {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<FichaPerfilReadModel> ejecutar(FichaPerfilCriteria criteria) {
        logger.debug(FichasMessages.FichaPerfil.LOG_CONSULTANDO, criteria.getPagina(), criteria.getTamanio());

        PaginatedResult<FichaPerfilReadModel> resultado = fichaPerfilQueryOutputPort.consultarTodas(criteria);

        logger.info(FichasMessages.FichaPerfil.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return resultado;
    }
}
