package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilValidatorImpl implements ModificarItemFichaPerfilValidator {

    private final FichaPerfilDelItemFinder fichaPerfilDelItemFinder;
    private final ItemFichaPropiaRule itemFichaPropiaRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

    @Override
    public void validar(UUID item, UUID estudiante) {
        UUID fichaPerfil = fichaPerfilDelItemFinder.obtener(item);

        itemFichaPropiaRule.validar(new PropietarioFichaCriteria(fichaPerfil, estudiante));
        estadoFichaPerfilEnTerminalRule.validar(fichaPerfil);
    }
}
