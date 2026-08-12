package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.DisponibilidadTituloFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaAsesorFicha;
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
    public void validar(FichaPerfilDomain ficha, boolean asesorExiste, boolean tituloYaExiste) {
        asesorFichaExisteRule.validar(new ExistenciaAsesorFicha(ficha.getAsesorFicha(), asesorExiste));
        fichaPerfilTituloUnicoRule.validar(
                new DisponibilidadTituloFicha(ficha.getTituloProyecto(), tituloYaExiste));
    }
}
