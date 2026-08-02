package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilValidator {

    private final ItemFichaPropiaRule itemFichaPropiaRule;

    public void validar(UUID fichaPerfil, UUID estudiante) {
        itemFichaPropiaRule.validar(new PropietarioFichaCriteria(fichaPerfil, estudiante));
    }
}
