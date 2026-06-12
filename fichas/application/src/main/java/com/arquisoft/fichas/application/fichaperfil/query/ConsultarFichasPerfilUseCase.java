package com.arquisoft.fichas.application.fichaperfil.query;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.in.ConsultarFichasPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultarFichasPerfilUseCase implements ConsultarFichasPerfilInputPort {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Override
    public PaginatedResult<FichaPerfilReadModel> ejecutar(FichaPerfilCriteria criteria) {
        log.debug(FichasMessages.FichaPerfil.LOG_CONSULTANDO, criteria.getPagina(), criteria.getTamanio());

        PaginatedResult<FichaPerfilReadModel> resultado = fichaPerfilQueryOutputPort.consultarTodas(criteria);

        log.info(FichasMessages.FichaPerfil.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return resultado;
    }
}
