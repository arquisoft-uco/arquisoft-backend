package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.DisponibilidadTipoItem;
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
    public void validar(ItemFichaPerfilDomain item, UUID estudiante, boolean fichaExiste, boolean esPropietario,
                        boolean tipoYaExiste) {

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(item.getFichaPerfilId(), fichaExiste));
        itemFichaPropiaRule.validar(new PropiedadFicha(item.getFichaPerfilId(), estudiante, esPropietario));
        itemTipoNoDuplicadoRule.validar(new DisponibilidadTipoItem(item.getTipoItem(), tipoYaExiste));
    }
}
