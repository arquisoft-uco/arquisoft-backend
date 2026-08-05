package com.arquisoft.fichas.application.fichaperfil.command.validator.impl;

import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.ModificarFichaPerfilDomain;
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
    public void validar(ModificarFichaPerfilDomain modificacion, FichaPerfilDomain fichaActual) {
        estudiantePropietarioFichaRule.validar(
                new PropietarioFichaCriteria(modificacion.getFichaPerfil(), modificacion.getEstudiante()));
        fichaPerfilTituloDisponibleRule.validar(
                new TituloFichaCriteria(fichaActual.getTituloProyecto(), modificacion.getTituloProyecto()));
    }
}
