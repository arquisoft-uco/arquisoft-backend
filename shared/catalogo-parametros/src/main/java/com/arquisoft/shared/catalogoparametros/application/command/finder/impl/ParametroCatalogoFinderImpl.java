package com.arquisoft.shared.catalogoparametros.application.command.finder.impl;

import com.arquisoft.shared.catalogoparametros.application.command.finder.ParametroCatalogoFinder;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.ParametrosCatalogoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParametroCatalogoFinderImpl implements ParametroCatalogoFinder {

    private final ParametrosCatalogoOutputPort parametrosCatalogoOutputPort;

    @Override
    public String obtener(String nombre) {
        return parametrosCatalogoOutputPort.valor(nombre);
    }
}
