package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilValidatorImpl implements RegistrarFichaPerfilValidator {

    private final AsesorFichaExisteRule asesorFichaExisteRule;
    private final FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule;

    @Override
    public void validar(FichaPerfilDomain ficha) {
        asesorFichaExisteRule.validar(ficha.getAsesorFicha());
        fichaPerfilTituloUnicoRule.validar(ficha.getTituloProyecto());
    }
}
