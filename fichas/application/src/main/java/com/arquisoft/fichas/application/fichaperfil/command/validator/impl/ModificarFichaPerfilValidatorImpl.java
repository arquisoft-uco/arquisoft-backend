package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl.EstudiantePropietarioFichaRuleImpl;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.DisponibilidadTituloFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.impl.FichaPerfilTituloUnicoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarFichaPerfilValidatorImpl implements ModificarFichaPerfilValidator {

    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;
    private final FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule;

    // Las Rules no son beans y no necesitan serlo: son funciones puras, sin estado ni
    // dependencias, asi que no hay variabilidad ni ciclo de vida que un contenedor deba
    // gestionar. Construirlas aqui deja en un unico sitio que reglas ejecuta este
    // validator, y elimina el bean por regla que habia que recordar en cada regla nueva.
    public ModificarFichaPerfilValidatorImpl() {
        this.estudiantePropietarioFichaRule = new EstudiantePropietarioFichaRuleImpl();
        this.fichaPerfilTituloUnicoRule = new FichaPerfilTituloUnicoRuleImpl();
    }

    @Override
    public void validar(ModificacionFichaPerfilDomain modificacion, boolean esPropietario,
                        boolean tituloYaExiste) {

        estudiantePropietarioFichaRule.validar(new PropiedadFicha(
                modificacion.getFichaPerfil(), modificacion.getEstudiante(), esPropietario));

        fichaPerfilTituloUnicoRule.validar(
                new DisponibilidadTituloFicha(modificacion.getTituloProyecto(), tituloYaExiste));
    }
}
