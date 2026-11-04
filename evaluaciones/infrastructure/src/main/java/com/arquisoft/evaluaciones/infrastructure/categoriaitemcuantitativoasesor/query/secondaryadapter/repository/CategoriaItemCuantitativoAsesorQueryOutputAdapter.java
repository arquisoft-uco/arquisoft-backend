package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.criteria.CategoriaItemCuantitativoAsesorCriteria;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.secondaryport.CategoriaItemCuantitativoAsesorQueryOutputPort;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.secondaryadapter.repository.mapper.CategoriaItemCuantitativoAsesorQueryMapper;
import com.arquisoft.shared.util.UtilObjeto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoriaItemCuantitativoAsesorQueryOutputAdapter
        implements CategoriaItemCuantitativoAsesorQueryOutputPort {

    private final CategoriaItemCuantitativoAsesorQueryRepository repository;

    @Override
    public List<CategoriaItemCuantitativoAsesorReadModel> consultar(CategoriaItemCuantitativoAsesorCriteria criteria) {
        var entidades = UtilObjeto.esNulo(criteria.nombre())
                ? repository.findAllByOrderByNombreAsc()
                : repository.findByNombreContainingIgnoreCaseOrderByNombreAsc(criteria.nombre());

        return entidades.stream()
                .map(CategoriaItemCuantitativoAsesorQueryMapper::toReadModel)
                .toList();
    }
}
