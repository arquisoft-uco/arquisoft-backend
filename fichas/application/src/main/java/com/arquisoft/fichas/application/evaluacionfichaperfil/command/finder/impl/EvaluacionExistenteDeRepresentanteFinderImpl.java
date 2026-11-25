package com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.EvaluacionExistenteDeRepresentanteFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper.EvaluacionExistenteMapper;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionExistente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionExistenteDeRepresentanteFinderImpl implements EvaluacionExistenteDeRepresentanteFinder {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Override
    public EvaluacionExistente obtener(EvaluacionFichaPerfilDomain evaluacion) {
        return evaluacionFichaPerfilOutputPort.obtenerEvaluacionDeRepresentanteEnFicha(
                        evaluacion.getRepresentanteComiteId(), evaluacion.getFichaPerfilId())
                .map(EvaluacionExistenteMapper::toDomain)
                .orElse(EvaluacionExistente.VACIO);
    }
}
