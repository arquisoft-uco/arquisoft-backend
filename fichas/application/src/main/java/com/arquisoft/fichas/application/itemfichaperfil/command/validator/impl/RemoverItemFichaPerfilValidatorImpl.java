package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.model.FichaPerfilDelItem;
import com.arquisoft.fichas.domain.itemfichaperfil.model.RevisionesItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilValidatorImpl implements RemoverItemFichaPerfilValidator {

    private final ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;
    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;
    private final ItemSinRevisionesRule itemSinRevisionesRule;

    @Override
    public void validar(UUID item, UUID estudiante, Optional<UUID> fichaDelItem, boolean esPropietario,
                        long totalRevisiones) {

        itemFichaPerfilExisteRule.validar(new FichaPerfilDelItem(item, fichaDelItem));

        fichaDelItem.ifPresent(fichaPerfil -> estudiantePropietarioFichaRule.validar(
                new PropiedadFicha(fichaPerfil, estudiante, esPropietario)));

        itemSinRevisionesRule.validar(new RevisionesItem(item, totalRevisiones));
    }
}
