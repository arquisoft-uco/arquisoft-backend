package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.NombreItemCuantitativoJuradoPorCategoriaExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NombreItemCuantitativoJuradoPorCategoriaExisteFinderImpl
        implements NombreItemCuantitativoJuradoPorCategoriaExisteFinder {

    private final ItemCuantitativoJuradoOutputPort outputPort;

    @Override
    public Boolean obtener(ItemCuantitativoJuradoDomain item) {
        return outputPort.existePorNombreYCategoriaIgnorandoMayusculas(
                item.getNombre(), item.getCategoria());
    }
}
