package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.secondaryport.ItemFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.mapper.ItemFichaPerfilEstudianteQueryMapper;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.mapper.ItemFichaPerfilQueryMapper;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository.mapper.ItemFichaPerfilRepresentanteQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemFichaPerfilQueryOutputAdapter implements ItemFichaPerfilQueryOutputPort {

    private final ItemFichaPerfilQueryRepository itemFichaPerfilQueryRepository;
    private final ItemFichaPerfilEstudianteQueryRepository itemFichaPerfilEstudianteQueryRepository;
    private final ItemFichaPerfilRepresentanteQueryRepository itemFichaPerfilRepresentanteQueryRepository;

    @Override
    public List<ItemFichaPerfilReadModel> consultarPorFichaYAsesor(UUID fichaPerfil, UUID asesorFicha) {
        return itemFichaPerfilQueryRepository
                .findByFichaPerfilIdAndAsesorIdOrderByTipoItemNombreAsc(fichaPerfil, asesorFicha)
                .stream()
                .map(ItemFichaPerfilQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<ItemFichaPerfilReadModel> consultarPorFichaYEstudiante(UUID fichaPerfil, UUID estudiante) {
        return itemFichaPerfilEstudianteQueryRepository
                .findByFichaPerfilIdAndEstudianteIdOrderByTipoItemNombreAsc(fichaPerfil, estudiante)
                .stream()
                .map(ItemFichaPerfilEstudianteQueryMapper::toReadModel)
                .toList();
    }

    @Override
    public List<ItemFichaPerfilReadModel> consultarPorFichaYRepresentante(UUID fichaPerfil, UUID representanteComite) {
        return itemFichaPerfilRepresentanteQueryRepository
                .findByFichaPerfilIdAndRepresentanteComiteIdOrderByTipoItemNombreAsc(fichaPerfil, representanteComite)
                .stream()
                .map(ItemFichaPerfilRepresentanteQueryMapper::toReadModel)
                .toList();
    }
}
