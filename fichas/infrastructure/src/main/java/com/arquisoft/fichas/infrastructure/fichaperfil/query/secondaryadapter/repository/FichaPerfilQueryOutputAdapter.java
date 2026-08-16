package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper.FichaPerfilQueryMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilQueryOutputAdapter implements FichaPerfilQueryOutputPort {

    private final FichaPerfilQueryRepository fichaPerfilRepository;
    private final FichaPerfilJpaSpecification specification;

    @Override
    public PaginatedResult<FichaPerfilReadModel> consultarTodas(FichaPerfilCriteria criteria) {
        Pageable pageable = PageableMapper.toPageable(criteria, FichaPerfilSortMapper::traducir);
        Specification<FichaPerfilJpaQueryEntity> spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                fichaPerfilRepository.findAll(spec, pageable)
                        .map(FichaPerfilQueryMapper::toReadModel));
    }

    @Override
    public boolean existePorId(UUID id) {
        return fichaPerfilRepository.existsById(id);
    }
}
