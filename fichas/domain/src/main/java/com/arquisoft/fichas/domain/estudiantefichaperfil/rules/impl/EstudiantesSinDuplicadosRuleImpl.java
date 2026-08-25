package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesSinDuplicadosRule;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;
import java.util.UUID;

public class EstudiantesSinDuplicadosRuleImpl implements EstudiantesSinDuplicadosRule {

    @Override
    public void validar(List<UUID> estudiantes) {
        UtilColeccion.primerDuplicado(estudiantes)
                .ifPresent(duplicado -> {
                    throw new EstudianteDuplicadoException((UUID) duplicado);
                });
    }
}
