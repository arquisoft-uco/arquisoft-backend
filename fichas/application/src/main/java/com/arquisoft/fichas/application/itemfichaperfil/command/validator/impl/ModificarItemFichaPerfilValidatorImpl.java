package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.ExistenciaEstadoFichaPerfil;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilEnTerminalRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ExistenciaItemFichaPerfil;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemFichaPerfilExisteRuleImpl;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.impl.ItemFichaPropiaRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ModificarItemFichaPerfilValidatorImpl implements ModificarItemFichaPerfilValidator {

    private final ItemFichaPerfilExisteRule itemFichaPerfilExisteRule = new ItemFichaPerfilExisteRuleImpl();
    private final ItemFichaPropiaRule itemFichaPropiaRule = new ItemFichaPropiaRuleImpl();
    private final EstadoFichaPerfilExisteRule estadoFichaPerfilExisteRule = new EstadoFichaPerfilExisteRuleImpl();
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule = new EstadoFichaPerfilEnTerminalRuleImpl();

    @Override
    public void validar(UUID item, UUID estudiante, UUID fichaDelItem, boolean itemExiste,
                        boolean esPropietario, EstadoFichaPerfilDomain estadoActual) {

        itemFichaPerfilExisteRule.validar(new ExistenciaItemFichaPerfil(item, itemExiste));

        itemFichaPropiaRule.validar(new PropiedadFicha(fichaDelItem, estudiante, esPropietario));

        estadoFichaPerfilExisteRule.validar(
                new ExistenciaEstadoFichaPerfil(fichaDelItem, !estadoActual.esVacio()));
        estadoFichaPerfilEnTerminalRule.validar(
                new EstadoActualFicha(fichaDelItem, estadoActual.getEstadoFicha()));
    }
}
