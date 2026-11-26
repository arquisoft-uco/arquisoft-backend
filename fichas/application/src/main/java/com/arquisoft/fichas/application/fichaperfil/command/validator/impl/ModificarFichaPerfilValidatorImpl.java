package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.model.ExistenciaEstadoFichaPerfil;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnConstruccionRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilExisteRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilEnConstruccionRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilExisteRuleImpl;
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
    private final EstadoFichaPerfilExisteRule estadoFichaPerfilExisteRule;
    private final EstadoFichaPerfilEnConstruccionRule estadoFichaPerfilEnConstruccionRule;
    private final FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule;

    public ModificarFichaPerfilValidatorImpl() {
        this.estudiantePropietarioFichaRule = new EstudiantePropietarioFichaRuleImpl();
        this.estadoFichaPerfilExisteRule = new EstadoFichaPerfilExisteRuleImpl();
        this.estadoFichaPerfilEnConstruccionRule = new EstadoFichaPerfilEnConstruccionRuleImpl();
        this.fichaPerfilTituloUnicoRule = new FichaPerfilTituloUnicoRuleImpl();
    }

    @Override
    public void validar(ModificacionFichaPerfilDomain modificacion, boolean esPropietario,
                        EstadoFichaPerfilDomain estadoActual, boolean tituloYaExiste) {

        estudiantePropietarioFichaRule.validar(new PropiedadFicha(
                modificacion.getFichaPerfil(), modificacion.getEstudiante(), esPropietario));

        estadoFichaPerfilExisteRule.validar(
                new ExistenciaEstadoFichaPerfil(modificacion.getFichaPerfil(), !estadoActual.esVacio()));
        estadoFichaPerfilEnConstruccionRule.validar(
                new EstadoActualFicha(modificacion.getFichaPerfil(), estadoActual.getEstadoFicha()));

        fichaPerfilTituloUnicoRule.validar(
                new DisponibilidadTituloFicha(modificacion.getTituloProyecto(), tituloYaExiste));
    }
}
