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

    // Las Rules no son beans y no necesitan serlo: son funciones puras, sin estado ni
    // dependencias, asi que no hay variabilidad ni ciclo de vida que un contenedor deba
    // gestionar. Construirlas aqui deja en un unico sitio que reglas ejecuta este
    // validator, y elimina el bean por regla que habia que recordar en cada regla nueva.
    public AsignarEstadoInicialFichaPerfilValidatorImpl() {
        this.fichaPerfilExisteRule = new FichaPerfilExisteRuleImpl();
    }

    @Override
    public void validar(UUID fichaPerfil, boolean fichaExiste) {
        fichaPerfilExisteRule.validar(new ExistenciaFichaPerfil(fichaPerfil, fichaExiste));
    }
}
