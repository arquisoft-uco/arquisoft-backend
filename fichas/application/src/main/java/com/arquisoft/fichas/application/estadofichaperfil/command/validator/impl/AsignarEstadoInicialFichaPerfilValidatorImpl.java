package com.arquisoft.fichas.application.estadofichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AsignarEstadoInicialFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarEstadoInicialFichaPerfilValidatorImpl implements AsignarEstadoInicialFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;

    @Override
    public void validar(UUID fichaPerfil, boolean fichaExiste) {
        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(fichaPerfil, fichaExiste));
    }
}
