package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.command.secondaryport.CategoriaItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.CategoriaItemCuantitativoJuradoExisteFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoriaItemCuantitativoJuradoExisteFinderImpl
        implements CategoriaItemCuantitativoJuradoExisteFinder {

    private final CategoriaItemCuantitativoJuradoOutputPort outputPort;

    @Override
    public Boolean obtener(UUID categoriaId) {
        return outputPort.existePorId(categoriaId);
    }
}
