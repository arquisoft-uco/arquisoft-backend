package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.NombreItemCualitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NombreItemCualitativoAsesorExisteFinderImpl implements NombreItemCualitativoAsesorExisteFinder {

    private final ItemCualitativoAsesorOutputPort itemCualitativoAsesorOutputPort;

    @Override
    public Boolean obtener(String nombre) {
        return itemCualitativoAsesorOutputPort.existePorNombreIgnorandoMayusculas(nombre);
    }
}
