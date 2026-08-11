package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ItemTipoCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemTipoNoDuplicadoRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarItemFichaPerfilValidatorImpl implements AgregarItemFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final ItemFichaPropiaRule itemFichaPropiaRule;
    private final ItemTipoNoDuplicadoRule itemTipoNoDuplicadoRule;

    @Override
    public void validar(ItemFichaPerfilDomain item, UUID estudiante) {
        fichaPerfilExisteRule.validar(item.getFichaPerfilId());
        itemFichaPropiaRule.validar(new PropietarioFichaCriteria(item.getFichaPerfilId(), estudiante));
        itemTipoNoDuplicadoRule.validar(
                new ItemTipoCriteria(item.getFichaPerfilId(), item.getTipoItem().getId()));
    }
}
