package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.ExistenciaEstadoFichaPerfil;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilEnTerminalRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilExisteRuleImpl;
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
    private final EstadoFichaPerfilExisteRule estadoFichaPerfilExisteRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;
    private final ItemTipoNoDuplicadoRule itemTipoNoDuplicadoRule;

    public AgregarItemFichaPerfilValidatorImpl() {
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
        this.itemFichaPropiaRule = new ItemFichaPropiaRuleImpl();
        this.estadoFichaPerfilExisteRule = new EstadoFichaPerfilExisteRuleImpl();
        this.estadoFichaPerfilEnTerminalRule = new EstadoFichaPerfilEnTerminalRuleImpl();
        this.itemTipoNoDuplicadoRule = new ItemTipoNoDuplicadoRuleImpl();
    }

    @Override
    public void validar(ItemFichaPerfilDomain item, UUID estudiante, boolean fichaExiste, boolean esPropietario,
                        EstadoFichaPerfilDomain estadoActual, boolean tipoYaExiste) {

        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(item.getFichaPerfilId(), fichaExiste));
        itemFichaPropiaRule.validar(new PropiedadFicha(item.getFichaPerfilId(), estudiante, esPropietario));
        estadoFichaPerfilExisteRule.validar(
                new ExistenciaEstadoFichaPerfil(item.getFichaPerfilId(), !estadoActual.esVacio()));
        estadoFichaPerfilEnTerminalRule.validar(
                new EstadoActualFicha(item.getFichaPerfilId(), estadoActual.getEstadoFicha()));
        itemTipoNoDuplicadoRule.validar(new DisponibilidadTipoItem(item.getTipoItem(), tipoYaExiste));
    }
}
