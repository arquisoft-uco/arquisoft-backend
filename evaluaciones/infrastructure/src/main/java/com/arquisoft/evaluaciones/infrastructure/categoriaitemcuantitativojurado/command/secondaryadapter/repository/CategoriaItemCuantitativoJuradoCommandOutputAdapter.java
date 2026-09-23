package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.command.secondaryport.CategoriaItemCuantitativoJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoriaItemCuantitativoJuradoCommandOutputAdapter
        implements CategoriaItemCuantitativoJuradoOutputPort {

    private final CategoriaItemCuantitativoJuradoCommandRepository repository;

    @Override
    public boolean existePorId(UUID categoriaId) {
        return repository.existsById(categoriaId);
    }
}
