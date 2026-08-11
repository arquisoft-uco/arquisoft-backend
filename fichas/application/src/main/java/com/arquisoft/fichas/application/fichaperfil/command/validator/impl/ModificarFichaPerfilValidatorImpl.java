package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.model.TituloFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloDisponibleRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarFichaPerfilValidatorImpl implements ModificarFichaPerfilValidator {

    private final EstudiantePropietarioFichaRule estudiantePropietarioFichaRule;
    private final FichaPerfilTituloDisponibleRule fichaPerfilTituloDisponibleRule;

    @Override
    public void validar(ModificacionFichaPerfilDomain modificacion) {
        estudiantePropietarioFichaRule.validar(
                new PropietarioFichaCriteria(modificacion.getFichaPerfil(), modificacion.getEstudiante()));
        fichaPerfilTituloDisponibleRule.validar(
                new TituloFichaCriteria(modificacion.getFichaPerfil(), modificacion.getTituloProyecto()));
    }
}
