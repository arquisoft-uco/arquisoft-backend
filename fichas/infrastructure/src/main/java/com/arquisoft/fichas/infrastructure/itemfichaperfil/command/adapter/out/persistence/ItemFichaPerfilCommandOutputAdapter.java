package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence.ItemFichaPerfilMapper;
import com.arquisoft.fichas.infrastructure.tipoitem.persistence.TipoItemJpaEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemFichaPerfilCommandOutputAdapter implements ItemFichaPerfilOutputPort {

    private final ItemFichaPerfilJpaRepository jpaRepository;

    @PersistenceContext(unitName = "fichas")
    private EntityManager entityManager;

    @Override
    public void guardar(ItemFichaPerfilAggregate aggregate) {
        var tipoItemRef = entityManager.getReference(
                TipoItemJpaEntity.class,
                aggregate.getTipoItem().getId()
        );
        var entity = ItemFichaPerfilMapper.toJpaEntity(aggregate, tipoItemRef);
        jpaRepository.save(entity);
    }

    @Override
    public boolean existsPorFichaYTipoItem(UUID fichaPerfilId, String tipoItemCode) {
        return jpaRepository.existsByFichaPerfilIdAndTipoItemId(fichaPerfilId, tipoItemCode);
    }
}
