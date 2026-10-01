package com.arquisoft.fichas.domain.estudiante.rules.impl;

import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.fichas.domain.estudiante.rules.EstudiantesExistenRule;
import com.arquisoft.shared.util.UtilColeccion;

public class EstudiantesExistenRuleImpl implements EstudiantesExistenRule {

    @Override
    public void validar(ExistenciaEstudiantes existencia) {
        if (UtilColeccion.esVaciaONula(existencia.solicitados())) {
            return;
        }
        existencia.solicitados().stream()
                .filter(estudiante -> !existencia.existentes().contains(estudiante))
                .findFirst()
                .ifPresent(inexistente -> {
                    throw new EstudianteNoEncontradoException(inexistente);
                });
    }
}
