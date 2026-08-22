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

    // Las Rules no son beans y no necesitan serlo: son funciones puras, sin estado ni
    // dependencias, asi que no hay variabilidad ni ciclo de vida que un contenedor deba
    // gestionar. Construirlas aqui deja en un unico sitio que reglas ejecuta este
    // validator, y elimina el bean por regla que habia que recordar en cada regla nueva.
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
