package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.CategoriaItemCuantitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoriaItemCuantitativoJuradoExisteFinderImpl
        implements CategoriaItemCuantitativoJuradoExisteFinder {

    private final ItemCuantitativoJuradoOutputPort outputPort;

    @Override
    public Boolean obtener(UUID categoriaId) {
        return outputPort.existeCategoriaPorId(categoriaId);
    }
}
