package com.arquisoft.fichas.application.fichaperfil.query;

import com.arquisoft.fichas.application.fichaperfil.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.domain.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultarFichasPerfilUseCase implements ConsultarFichasPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<FichaPerfilReadModel> ejecutar(PaginationRequest request) {
        log.debug("Consultando fichas de perfil — page={}, size={}", request.getPage(), request.getSize());

        PaginatedResult<FichaPerfilReadModel> result = fichaPerfilOutputPort
                .consultarTodas(request)
                .map(FichaPerfilReadModel::fromDomain);

        log.info("Consulta fichas-perfil completada — total={}, page={}, size={}",
                result.getTotalElements(), request.getPage(), request.getSize());
        return result;
    }
}
