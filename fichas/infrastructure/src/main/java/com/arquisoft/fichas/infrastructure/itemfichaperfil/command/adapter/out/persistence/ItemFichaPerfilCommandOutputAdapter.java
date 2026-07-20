package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilMapper;
import com.arquisoft.fichas.infrastructure.tipoitem.persistence.TipoItemJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemFichaPerfilCommandOutputAdapter implements ItemFichaPerfilOutputPort {

    private final ItemFichaPerfilJpaRepository jpaRepository;
    private final TipoItemJpaRepository tipoItemJpaRepository;

    @Override
    public void guardar(ItemFichaPerfilAggregate aggregate) {
        var tipoItemRef = tipoItemJpaRepository.getReferenceById(aggregate.getTipoItem().getId());
        var entity = ItemFichaPerfilMapper.toJpaEntity(aggregate, tipoItemRef);
        jpaRepository.save(entity);
    }

    @Override
    public boolean existsPorFichaYTipoItem(UUID fichaPerfilId, String tipoItem) {
        return jpaRepository.existsByFichaPerfilIdAndTipoItemId(fichaPerfilId, tipoItem);
    }

    @Override
    public boolean existsById(UUID itemId) {
        return jpaRepository.existsById(itemId);
    }

    @Override
    public Optional<ItemFichaPerfilAggregate> buscarPorId(UUID itemId) {
        return jpaRepository.findById(itemId)
                .map(ItemFichaPerfilMapper::toDomain);
    }
}
