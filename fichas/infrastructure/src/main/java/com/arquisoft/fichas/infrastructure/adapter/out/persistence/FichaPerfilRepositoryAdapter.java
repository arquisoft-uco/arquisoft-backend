package com.arquisoft.fichas.infrastructure.adapter.out.persistence;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.out.FichaPerfilRepositoryPort;
import com.arquisoft.shared.domain.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FichaPerfilRepositoryAdapter implements FichaPerfilRepositoryPort {

    private final FichaPerfilJpaRepository fichaPerfilJpaRepository;

    @Override
    public Page<FichaPerfil> consultarPaginadas(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        org.springframework.data.domain.Page<FichaPerfilJpaEntity> springPage =
                fichaPerfilJpaRepository.findAll(pageRequest);

        List<FichaPerfil> content = springPage.getContent().stream()
                .map(FichaPerfilJpaEntity::toDomain)
                .toList();

        return Page.of(content, springPage.getNumber(), springPage.getSize(),
                springPage.getTotalElements());
    }
}
