package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.RegistrarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.DisponibilidadTituloFicha;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.AsesorFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilTituloUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarFichaPerfilValidatorImpl implements RegistrarFichaPerfilValidator {

    private final AsesorFichaExisteRule asesorFichaExisteRule;
    private final FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule;

    public RegistrarFichaPerfilValidatorImpl() {
        this.asesorFichaExisteRule = new AsesorFichaExisteRuleImpl();
        this.fichaPerfilTituloUnicoRule = new FichaPerfilTituloUnicoRuleImpl();
    }

    @Override
    public void validar(FichaPerfilDomain ficha, boolean asesorExiste, boolean tituloYaExiste) {
        asesorFichaExisteRule.validar(new ExistenciaAsesorFicha(ficha.getAsesorFicha(), asesorExiste));
        fichaPerfilTituloUnicoRule.validar(
                new DisponibilidadTituloFicha(ficha.getTituloProyecto(), tituloYaExiste));
    }
}
