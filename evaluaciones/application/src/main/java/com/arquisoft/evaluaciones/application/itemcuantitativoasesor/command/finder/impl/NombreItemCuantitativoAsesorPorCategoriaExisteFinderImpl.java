package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.NombreItemCuantitativoAsesorPorCategoriaExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NombreItemCuantitativoAsesorPorCategoriaExisteFinderImpl
        implements NombreItemCuantitativoAsesorPorCategoriaExisteFinder {

    private final ItemCuantitativoAsesorOutputPort itemCuantitativoAsesorOutputPort;

    @Override
    public Boolean obtener(ItemCuantitativoAsesorDomain item) {
        return itemCuantitativoAsesorOutputPort.existePorNombreYCategoriaIgnorandoMayusculas(
                item.getNombre(), item.getCategoria());
    }
}
