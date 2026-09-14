package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.secondaryport.CategoriaItemCuantitativoJuradoQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.secondaryadapter.repository.mapper.CategoriaItemCuantitativoJuradoQueryMapper;
import com.arquisoft.shared.util.UtilObjeto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoriaItemCuantitativoJuradoQueryOutputAdapter
        implements CategoriaItemCuantitativoJuradoQueryOutputPort {

    private final CategoriaItemCuantitativoJuradoQueryRepository repository;

    @Override
    public List<CategoriaItemCuantitativoJuradoReadModel> consultar(String nombre) {
        var entidades = UtilObjeto.esNulo(nombre)
                ? repository.findAllByOrderByNombreAsc()
                : repository.findByNombreContainingIgnoreCaseOrderByNombreAsc(nombre);

        return entidades.stream()
                .map(CategoriaItemCuantitativoJuradoQueryMapper::toReadModel)
                .toList();
    }
}
