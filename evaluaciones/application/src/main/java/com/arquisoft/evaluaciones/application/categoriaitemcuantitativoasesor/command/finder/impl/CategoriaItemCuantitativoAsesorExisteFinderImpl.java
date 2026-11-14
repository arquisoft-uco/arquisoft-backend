package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.command.finder.CategoriaItemCuantitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.command.secondaryport.CategoriaItemCuantitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoriaItemCuantitativoAsesorExisteFinderImpl
        implements CategoriaItemCuantitativoAsesorExisteFinder {

    private final CategoriaItemCuantitativoAsesorOutputPort categoriaItemCuantitativoAsesorOutputPort;

    @Override
    public Boolean obtener(UUID categoria) {
        return categoriaItemCuantitativoAsesorOutputPort.existePorId(categoria);
    }
}
