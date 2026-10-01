package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.NombreItemCualitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NombreItemCualitativoJuradoExisteFinderImpl
        implements NombreItemCualitativoJuradoExisteFinder {

    private final ItemCualitativoJuradoOutputPort itemCualitativoJuradoOutputPort;

    @Override
    public Boolean obtener(String nombre) {
        return itemCualitativoJuradoOutputPort.existePorNombreIgnorandoMayusculas(nombre);
    }
}
