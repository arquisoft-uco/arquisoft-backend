package com.arquisoft.biblioteca.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.biblioteca.domain.entregableproyectogrado.exception.ArchivoEntregableNoDisponibleException;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.model.UbicacionArchivoEntregable;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.rules.ArchivoEntregableDisponibleRule;
import com.arquisoft.shared.util.UtilTexto;

public class ArchivoEntregableDisponibleRuleImpl implements ArchivoEntregableDisponibleRule {

    @Override
    public void validar(UbicacionArchivoEntregable ubicacion) {
        if (UtilTexto.esVacioONulo(ubicacion.claveObjeto())) {
            throw new ArchivoEntregableNoDisponibleException(ubicacion.entregableProyectoGrado());
        }
    }
}
