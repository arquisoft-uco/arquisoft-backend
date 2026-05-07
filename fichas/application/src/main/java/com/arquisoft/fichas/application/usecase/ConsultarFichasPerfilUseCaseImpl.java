package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.in.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.domain.port.out.FichaPerfilRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultarFichasPerfilUseCaseImpl implements ConsultarFichasPerfilUseCase {

    private final FichaPerfilRepositoryPort fichaPerfilRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public Page<FichaPerfil> ejecutar(Pageable pageable) {
        log.debug("Consultando fichas de perfil — pageable={}", pageable);
        return fichaPerfilRepositoryPort.consultarTodas(pageable);
    }
}
