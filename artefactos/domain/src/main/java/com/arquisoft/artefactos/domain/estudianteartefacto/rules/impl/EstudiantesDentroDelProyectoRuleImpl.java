package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudiantesSuperanProyectoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.CupoEstudiantesArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesDentroDelProyectoRule;

import java.util.HashSet;

public class EstudiantesDentroDelProyectoRuleImpl implements EstudiantesDentroDelProyectoRule {

    @Override
    public void validar(CupoEstudiantesArtefacto cupo) {
        var resultado = new HashSet<>(cupo.vinculadosVigentes());
        resultado.addAll(cupo.solicitados());
        var maximo = cupo.vigentesProyecto().size();
        if (resultado.size() > maximo) {
            throw new EstudiantesSuperanProyectoException(maximo, resultado.size());
        }
    }
}
