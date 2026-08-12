package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.DisponibilidadTituloFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarFichaPerfilValidatorImpl implements ModificarFichaPerfilValidator {

    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;
    private final FichaPerfilTituloUnicoRule fichaPerfilTituloUnicoRule;

    @Override
    public void validar(ModificacionFichaPerfilDomain modificacion, boolean esPropietario,
                        boolean tituloYaExiste) {

        estudiantePropietarioFichaRule.validar(new PropiedadFicha(
                modificacion.getFichaPerfil(), modificacion.getEstudiante(), esPropietario));

        fichaPerfilTituloUnicoRule.validar(
                new DisponibilidadTituloFicha(modificacion.getTituloProyecto(), tituloYaExiste));
    }
}
