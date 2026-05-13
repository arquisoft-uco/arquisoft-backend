package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.in.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.domain.port.out.FichaPerfilRepositoryPort;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultarFichasPerfilUseCaseImpl implements ConsultarFichasPerfilUseCase {

    private final FichaPerfilRepositoryPort fichaPerfilRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<FichaPerfil> ejecutar(PaginationRequest request) {
        log.debug("Consultando fichas de perfil — page={}, size={}", request.getPage(), request.getSize());
        return fichaPerfilRepositoryPort.consultarTodas(request);
    }
}
