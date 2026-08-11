package com.arquisoft.fichas.application.itemfichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropietarioFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilValidatorImpl implements RemoverItemFichaPerfilValidator {

    private final FichaPerfilDelItemFinder fichaPerfilDelItemFinder;
    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;

    @Override
    public void validar(UUID item, UUID estudiante) {
        UUID fichaPerfil = fichaPerfilDelItemFinder.obtener(item);

        estudiantePropietarioFichaRule.validar(new PropietarioFicha(fichaPerfil, estudiante));
    }
}
