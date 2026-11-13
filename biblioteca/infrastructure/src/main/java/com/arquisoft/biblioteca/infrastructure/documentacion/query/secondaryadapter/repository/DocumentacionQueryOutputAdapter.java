package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.DocumentacionReadModel;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.DocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.mapper.DocumentacionQueryMapper;
import com.arquisoft.shared.jpa.util.PageableMapper;
import com.arquisoft.shared.jpa.util.PaginationMapper;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentacionQueryOutputAdapter implements DocumentacionQueryOutputPort {

    private static final String RUTA_DESEMPATE = "id";

    private final DocumentacionQueryRepository documentacionQueryRepository;
    private final DocumentacionJpaSpecification specification;

    @Override
    public PaginatedResult<DocumentacionReadModel> consultar(DocumentacionCriteria criteria) {
        var pageable = PageableMapper.toPageable(criteria, DocumentacionSortMapper::traducir, RUTA_DESEMPATE);
        var spec = specification.desdeCriteria(criteria);

        return PaginationMapper.toResult(
                documentacionQueryRepository.findAll(spec, pageable)
                        .map(DocumentacionQueryMapper::toReadModel));
    }
}
