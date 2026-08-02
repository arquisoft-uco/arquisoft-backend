package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilMapper;
import com.arquisoft.fichas.infrastructure.tipoitem.persistence.TipoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemFichaPerfilCommandOutputAdapter implements ItemFichaPerfilOutputPort {

    private final ItemFichaPerfilRepository jpaRepository;
    private final TipoItemRepository tipoItemRepository;

    @Override
    public void guardar(ItemFichaPerfilAggregate aggregate) {
        var tipoItemRef = tipoItemRepository.getReferenceById(aggregate.getTipoItem().getId());
        var entity = ItemFichaPerfilMapper.toEntity(aggregate, tipoItemRef);
        jpaRepository.save(entity);
    }

    @Override
    public boolean existePorFichaYTipoItem(UUID fichaPerfilId, String tipoItem) {
        return jpaRepository.existsByFichaPerfilIdAndTipoItemId(fichaPerfilId, tipoItem);
    }

    @Override
    public boolean existePorId(UUID itemId) {
        return jpaRepository.existsById(itemId);
    }

    @Override
    public Optional<ItemFichaPerfilAggregate> buscarPorId(UUID itemId) {
        return jpaRepository.findById(itemId)
                .map(ItemFichaPerfilMapper::toDomain);
    }

    @Override
    public void eliminarPorId(UUID itemId) {
        jpaRepository.deleteById(itemId);
    }
}
