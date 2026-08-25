package com.arquisoft.fichas.application.estadofichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AsignarEstadoInicialFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilExisteRuleImpl;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AsignarEstadoInicialFichaPerfilValidatorImpl implements AsignarEstadoInicialFichaPerfilValidator {

    private final FichaPerfilExisteRule fichaPerfilExisteRule;

    public AsignarEstadoInicialFichaPerfilValidatorImpl() {
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
    }

    @Override
    public void validar(UUID fichaPerfil, boolean fichaExiste) {
        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(fichaPerfil, fichaExiste));
    }
}
