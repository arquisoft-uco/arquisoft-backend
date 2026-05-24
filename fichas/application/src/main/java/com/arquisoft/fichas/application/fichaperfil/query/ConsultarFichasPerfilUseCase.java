package com.arquisoft.fichas.application.fichaperfil.query;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.in.ConsultarFichasPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultarFichasPerfilUseCase implements ConsultarFichasPerfilInputPort {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<FichaPerfilReadModel> ejecutar(FichaPerfilCriteria criteria) {
        log.debug("Consultando fichas de perfil — page={}, size={}", criteria.getPagina(), criteria.getTamanio());

        PaginatedResult<FichaPerfilReadModel> result = fichaPerfilQueryOutputPort.consultarTodas(criteria);

        log.info("Consulta fichas-perfil completada — total={}, page={}, size={}",
                result.getTotalElements(), criteria.getPagina(), criteria.getTamanio());
        return result;
    }
}
