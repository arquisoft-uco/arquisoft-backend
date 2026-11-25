package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilDisponibleEvaluacionQueryOutputPort;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper.FichaPerfilDisponibleEvaluacionQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FichaPerfilDisponibleEvaluacionQueryOutputAdapter
        implements FichaPerfilDisponibleEvaluacionQueryOutputPort {

    private final FichaPerfilDisponibleEvaluacionQueryRepository fichaPerfilDisponibleEvaluacionRepository;
    private final FichaPerfilDisponibleEvaluacionJpaSpecification specification;

    @Override
    public PaginatedResult<FichaPerfilReadModel> consultarDisponiblesParaEvaluar(
            FichaPerfilDisponibleEvaluacionCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, FichaPerfilDisponibleEvaluacionSortMapper::traducir);
        var spec = specification.sinEvaluacionDe(criteria.getRepresentanteComite())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                fichaPerfilDisponibleEvaluacionRepository.findAll(spec, pageable)
                        .map(FichaPerfilDisponibleEvaluacionQueryMapper::toReadModel));
    }
}
