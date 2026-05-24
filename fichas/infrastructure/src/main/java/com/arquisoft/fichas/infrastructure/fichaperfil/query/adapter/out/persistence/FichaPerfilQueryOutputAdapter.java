package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.exception.OrdenamientoInvalidoException;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilMapper;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.SortDirection;
import com.arquisoft.shared.web.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FichaPerfilQueryOutputAdapter implements FichaPerfilQueryOutputPort {

    private final FichaPerfilJpaRepository fichaPerfilJpaRepository;

    @Override
    public PaginatedResult<FichaPerfilReadModel> consultarTodas(FichaPerfilCriteria criteria) {
        Pageable pageable = toPageable(criteria);
        Specification<FichaPerfilJpaEntity> spec = FichaPerfilJpaSpecification.desdeCriteria(criteria);
        try {
            return PaginationMapper.toResult(
                    fichaPerfilJpaRepository.findAll(spec, pageable)
                            .map(FichaPerfilMapper::toReadModel));
        } catch (PropertyReferenceException ex) {
            log.warn("Campo de ordenamiento inválido: {}", ex.getPropertyName());
            throw new OrdenamientoInvalidoException(ex.getPropertyName(), ex);
        } catch (InvalidDataAccessApiUsageException ex) {
            log.warn("Uso inválido de la API de acceso a datos al ordenar: {}", ex.getMessage());
            throw new OrdenamientoInvalidoException(pageable.getSort().toString(), ex);
        }
    }

    private Pageable toPageable(FichaPerfilCriteria criteria) {
        if (criteria.tieneOrden()) {
            Sort sort = Sort.by(
                    criteria.getDireccion() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC,
                    criteria.getOrdenarPor()
            );
            return PageRequest.of(criteria.getPagina(), criteria.getTamanio(), sort);
        }
        return PageRequest.of(criteria.getPagina(), criteria.getTamanio());
    }
}
