package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.ExistenciaEstadoFichaPerfil;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ExistenciaItemFichaPerfil;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilValidatorImpl implements ModificarItemFichaPerfilValidator {

    private final ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;
    private final ItemFichaPropiaRule itemFichaPropiaRule;
    private final EstadoFichaPerfilExisteRule estadoFichaPerfilExisteRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

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
