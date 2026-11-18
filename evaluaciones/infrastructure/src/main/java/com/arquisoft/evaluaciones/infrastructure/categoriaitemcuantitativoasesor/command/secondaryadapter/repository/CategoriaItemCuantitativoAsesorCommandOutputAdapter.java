package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.command.secondaryport.CategoriaItemCuantitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoriaItemCuantitativoAsesorCommandOutputAdapter
        implements CategoriaItemCuantitativoAsesorOutputPort {

    private final CategoriaItemCuantitativoAsesorCommandRepository repository;

    @Override
    public boolean existePorId(UUID categoriaId) {
        return repository.existsById(categoriaId);
    }
}
