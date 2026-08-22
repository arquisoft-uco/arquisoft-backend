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

    private final ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;
    private final ItemFichaPropiaRule itemFichaPropiaRule;
    private final EstadoFichaPerfilExisteRule estadoFichaPerfilExisteRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

    // Las Rules no son beans y no necesitan serlo: son funciones puras, sin estado ni
    // dependencias, asi que no hay variabilidad ni ciclo de vida que un contenedor deba
    // gestionar. Construirlas aqui deja en un unico sitio que reglas ejecuta este
    // validator, y elimina el bean por regla que habia que recordar en cada regla nueva.
    public ModificarItemFichaPerfilValidatorImpl() {
        this.itemFichaPerfilExisteRule = new ItemFichaPerfilExisteRuleImpl();
        this.itemFichaPropiaRule = new ItemFichaPropiaRuleImpl();
        this.estadoFichaPerfilExisteRule = new EstadoFichaPerfilExisteRuleImpl();
        this.estadoFichaPerfilEnTerminalRule = new EstadoFichaPerfilEnTerminalRuleImpl();
    }

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
