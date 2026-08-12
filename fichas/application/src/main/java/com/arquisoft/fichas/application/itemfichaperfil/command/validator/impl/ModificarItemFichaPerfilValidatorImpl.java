package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.model.FichaPerfilDelItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import com.arquisoft.shared.util.UtilText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilValidatorImpl implements ModificarItemFichaPerfilValidator {

    private final ItemFichaPerfilExisteRule itemFichaPerfilExisteRule;
    private final ItemFichaPropiaRule itemFichaPropiaRule;
    private final EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

    @Override
    public void validar(UUID item, UUID estudiante, Optional<UUID> fichaDelItem, boolean esPropietario,
                        Optional<String> estadoActual) {

        itemFichaPerfilExisteRule.validar(new FichaPerfilDelItem(item, fichaDelItem));

        fichaDelItem.ifPresent(fichaPerfil -> {
            itemFichaPropiaRule.validar(new PropiedadFicha(fichaPerfil, estudiante, esPropietario));
            estadoFichaPerfilEnTerminalRule.validar(
                    new EstadoActualFicha(fichaPerfil, estadoActual.orElse(UtilText.EMPTY)));
        });
    }
}
