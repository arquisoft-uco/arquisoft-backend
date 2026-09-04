package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.secondaryport.ItemFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.mapper.ItemFichaPerfilQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemFichaPerfilQueryOutputAdapter implements ItemFichaPerfilQueryOutputPort {

    private final ItemFichaPerfilQueryRepository itemFichaPerfilQueryRepository;

    @Override
    public List<ItemFichaPerfilReadModel> consultarPorFichaYAsesor(UUID fichaPerfil, UUID asesorFicha) {
        return itemFichaPerfilQueryRepository
                .findByFichaPerfilIdAndAsesorIdOrderByTipoItemNombreAsc(fichaPerfil, asesorFicha)
                .stream()
                .map(ItemFichaPerfilQueryMapper::toReadModel)
                .toList();
    }
}
