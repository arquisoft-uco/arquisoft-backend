package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.model.DisponibilidadTipoItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemFichaPropiaRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemTipoNoDuplicadoRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemTipoNoDuplicadoRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgregarItemFichaPerfilValidatorImpl implements AgregarItemFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;
    private final ItemFichaPropiaRule itemFichaPropiaRule;
    private final ItemTipoNoDuplicadoRule itemTipoNoDuplicadoRule;

    public AgregarItemFichaPerfilValidatorImpl() {
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.itemFichaPropiaRule = new ItemFichaPropiaRuleImpl();
        this.itemTipoNoDuplicadoRule = new ItemTipoNoDuplicadoRuleImpl();
    }

    @Override
    public void validar(ItemFichaPerfilDomain item, UUID estudiante, boolean fichaExiste, boolean esPropietario,
                        boolean tipoYaExiste) {

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(item.getFichaPerfilId(), fichaExiste));
        itemFichaPropiaRule.validar(new PropiedadFicha(item.getFichaPerfilId(), estudiante, esPropietario));
        itemTipoNoDuplicadoRule.validar(new DisponibilidadTipoItem(item.getTipoItem(), tipoYaExiste));
    }
}
