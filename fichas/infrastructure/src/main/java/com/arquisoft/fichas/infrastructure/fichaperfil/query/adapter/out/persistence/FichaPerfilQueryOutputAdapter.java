package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.exception.OrdenamientoInvalidoException;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilMapper;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.SortDirection;
import com.arquisoft.shared.postgres.util.PaginationMapper;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilQueryOutputAdapter implements FichaPerfilQueryOutputPort {

    private final FichaPerfilJpaRepository fichaPerfilJpaRepository;
    private final FichaPerfilJpaSpecification specification;
    private final EstudianteFichaPerfilJpaRepository estudianteFichaPerfilJpaRepository;
    private final AppLogger logger;

    @Override
    public PaginatedResult<FichaPerfilReadModel> consultarTodas(FichaPerfilCriteria criteria) {
        Pageable pageable = toPageable(criteria);
        Specification<FichaPerfilJpaEntity> spec = specification.desdeCriteria(criteria);
        try {
            return PaginationMapper.toResult(
                    fichaPerfilJpaRepository.findAll(spec, pageable)
                            .map(FichaPerfilMapper::toReadModel));
        } catch (PropertyReferenceException ex) {
            logger.warn(FichasMessages.FichaPerfil.LOG_ORDENAMIENTO_INVALIDO, ex.getPropertyName());
            throw new OrdenamientoInvalidoException(ex.getPropertyName(), ex);
        } catch (InvalidDataAccessApiUsageException ex) {
            logger.warn(FichasMessages.FichaPerfil.LOG_USO_INVALIDO_API_ORDEN, ex.getMessage());
            throw new OrdenamientoInvalidoException(pageable.getSort().toString(), ex);
        }
    }

    private Pageable toPageable(FichaPerfilCriteria criteria) {
        if (criteria.tieneOrden()) {
            List<Sort.Order> orders = criteria.getOrdenamiento().stream()
                    .map(o -> {
                        String ruta = FichaPerfilSortMapper.traducir(o.getCampo());
                        if (ruta == null) {
                            throw new OrdenamientoInvalidoException(o.getCampo());
                        }
                        return o.getDireccion() == SortDirection.ASC
                                ? Sort.Order.asc(ruta)
                                : Sort.Order.desc(ruta);
                    })
                    .toList();
            return PageRequest.of(criteria.getPagina(), criteria.getTamanio(), Sort.by(orders));
        }
        return PageRequest.of(criteria.getPagina(), criteria.getTamanio());
    }

    @Override
    public boolean existePorId(UUID id) {
        return fichaPerfilJpaRepository.existsById(id);
    }

    @Override
    public boolean esEstudiantePropietario(PropietarioFichaCriteria criteria) {
        return estudianteFichaPerfilJpaRepository.existsByFichaPerfilIdAndEstudianteId(
                criteria.fichaPerfil(),
                criteria.estudiante()
        );
    }
}
