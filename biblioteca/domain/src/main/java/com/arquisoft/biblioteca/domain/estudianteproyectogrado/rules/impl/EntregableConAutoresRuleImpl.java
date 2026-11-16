package com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.biblioteca.domain.estudianteproyectogrado.exception.EntregableSinAutoresException;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutoresEntregable;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.EntregableConAutoresRule;
import com.arquisoft.shared.util.UtilColeccion;

public class EntregableConAutoresRuleImpl implements EntregableConAutoresRule {

    @Override
    public void validar(AutoresEntregable autores) {
        if (UtilColeccion.esVaciaONula(autores.autores())) {
            throw new EntregableSinAutoresException(autores.entregableProyectoGrado());
        }
    }
}
