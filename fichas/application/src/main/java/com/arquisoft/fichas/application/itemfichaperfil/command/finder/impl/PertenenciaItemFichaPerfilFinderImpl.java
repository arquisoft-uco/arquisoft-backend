package com.arquisoft.fichas.application.itemfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.PertenenciaItemFichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.mapper.PertenenciaItemFichaPerfilMapper;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ItemDeEstudiante;
import com.arquisoft.fichas.domain.itemfichaperfil.model.PertenenciaItemFichaPerfil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PertenenciaItemFichaPerfilFinderImpl implements PertenenciaItemFichaPerfilFinder {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Override
    public PertenenciaItemFichaPerfil obtener(ItemDeEstudiante itemDeEstudiante) {
        return itemFichaPerfilOutputPort
                .obtenerPertenencia(itemDeEstudiante.item(), itemDeEstudiante.estudiante())
                .map(PertenenciaItemFichaPerfilMapper::toDomain)
                .orElse(PertenenciaItemFichaPerfil.VACIO);
    }
}
