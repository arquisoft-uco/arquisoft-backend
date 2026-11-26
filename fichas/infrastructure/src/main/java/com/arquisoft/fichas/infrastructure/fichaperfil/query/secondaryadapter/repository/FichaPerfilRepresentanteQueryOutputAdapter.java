package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilRepresentanteQueryOutputPort;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository.mapper.FichaPerfilRepresentanteQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FichaPerfilRepresentanteQueryOutputAdapter implements FichaPerfilRepresentanteQueryOutputPort {

    private final FichaPerfilRepresentanteQueryRepository fichaPerfilRepresentanteRepository;
    private final FichaPerfilRepresentanteJpaSpecification specification;

    @Override
    public PaginatedResult<FichaPerfilRepresentanteReadModel> consultarParticipacionRepresentante(
            FichaPerfilRepresentanteCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, FichaPerfilRepresentanteSortMapper::traducir, "id");
        var spec = specification.delRepresentante(criteria.getRepresentanteComite())
                .and(specification.desdeCriteria(criteria));

        return PaginationMapper.toResult(
                fichaPerfilRepresentanteRepository.findAll(spec, pageable)
                        .map(FichaPerfilRepresentanteQueryMapper::toReadModel));
    }
}
