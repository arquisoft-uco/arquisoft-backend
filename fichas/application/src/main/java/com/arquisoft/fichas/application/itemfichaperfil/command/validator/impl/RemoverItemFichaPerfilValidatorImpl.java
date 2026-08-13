package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ExistenciaItemFichaPerfil;
import com.arquisoft.fichas.domain.itemfichaperfil.model.RevisionesItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilValidatorImpl implements RemoverItemFichaPerfilValidator {

    private final ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;
    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;
    private final ItemSinRevisionesRule itemSinRevisionesRule;

    @Override
    public void validar(UUID item, UUID estudiante, UUID fichaDelItem, boolean itemExiste,
                        boolean esPropietario, long totalRevisiones) {

        itemFichaPerfilExisteRule.validar(new ExistenciaItemFichaPerfil(item, itemExiste));

        estudiantePropietarioFichaRule.validar(
                new PropiedadFicha(fichaDelItem, estudiante, esPropietario));

        itemSinRevisionesRule.validar(new RevisionesItem(item, totalRevisiones));
    }
}
