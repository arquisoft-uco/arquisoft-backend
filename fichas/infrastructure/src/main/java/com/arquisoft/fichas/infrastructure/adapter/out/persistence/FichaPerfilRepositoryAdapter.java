package com.arquisoft.fichas.infrastructure.adapter.out.persistence;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.out.FichaPerfilRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FichaPerfilRepositoryAdapter implements FichaPerfilRepositoryPort {

    private final FichaPerfilJpaRepository fichaPerfilJpaRepository;

    @Override
    public Page<FichaPerfil> consultarTodas(Pageable pageable) {
        return fichaPerfilJpaRepository.findAll(pageable).map(FichaPerfilJpaEntity::toDomain);
    }
}
