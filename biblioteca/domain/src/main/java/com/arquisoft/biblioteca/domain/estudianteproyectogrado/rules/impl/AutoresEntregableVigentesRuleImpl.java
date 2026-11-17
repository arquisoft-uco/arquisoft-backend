package com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.biblioteca.domain.estudianteproyectogrado.exception.AutorEntregableNoVigenteException;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutoresEntregable;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.rules.AutoresEntregableVigentesRule;

public class AutoresEntregableVigentesRuleImpl implements AutoresEntregableVigentesRule {

    @Override
    public void validar(AutoresEntregable autores) {
        var noVigentes = autores.autores().stream()
                .filter(autor -> !autor.vigente())
                .map(AutorDeEntregable::autor)
                .toList();
        if (!noVigentes.isEmpty()) {
            throw new AutorEntregableNoVigenteException(autores.entregableProyectoGrado(), noVigentes);
        }
    }
}
