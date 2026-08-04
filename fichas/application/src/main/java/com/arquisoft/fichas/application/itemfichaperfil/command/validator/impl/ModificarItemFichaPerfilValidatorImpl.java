package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilValidatorImpl implements ModificarItemFichaPerfilValidator {

    private final ItemFichaPropiaRule itemFichaPropiaRule;

    @Override
    public void validar(UUID fichaPerfil, UUID estudiante) {
        itemFichaPropiaRule.validar(new PropietarioFichaCriteria(fichaPerfil, estudiante));
    }
}
