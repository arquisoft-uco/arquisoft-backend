package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.criteria.CategoriaItemCuantitativoJuradoCriteria;
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
    public List<CategoriaItemCuantitativoJuradoReadModel> consultar(CategoriaItemCuantitativoJuradoCriteria criteria) {
        var entidades = UtilObjeto.esNulo(criteria.nombre())
                ? repository.findAllByOrderByNombreAsc()
                : repository.findByNombreContainingIgnoreCaseOrderByNombreAsc(criteria.nombre());

        return entidades.stream()
                .map(CategoriaItemCuantitativoJuradoQueryMapper::toReadModel)
                .toList();
    }
}
