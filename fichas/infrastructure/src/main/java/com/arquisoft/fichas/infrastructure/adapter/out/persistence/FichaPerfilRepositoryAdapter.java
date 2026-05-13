package com.arquisoft.fichas.infrastructure.adapter.out.persistence;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.out.FichaPerfilRepositoryPort;
import com.arquisoft.fichas.infrastructure.exception.OrdenamientoInvalidoException;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
import com.arquisoft.shared.web.util.PaginationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FichaPerfilRepositoryAdapter implements FichaPerfilRepositoryPort {

    private final FichaPerfilJpaRepository fichaPerfilJpaRepository;

    @Override
    public PaginatedResult<FichaPerfil> consultarTodas(PaginationRequest request) {
        Pageable pageable = PaginationMapper.toPageable(request);
        try {
            return PaginationMapper.toResult(
                    fichaPerfilJpaRepository.findAll(pageable)
                            .map(FichaPerfilJpaEntity::toDomain));
        } catch (PropertyReferenceException ex) {
            log.warn("Campo de ordenamiento inválido: {}", ex.getPropertyName());
            throw new OrdenamientoInvalidoException(ex.getPropertyName(), ex);
        } catch (InvalidDataAccessApiUsageException ex) {
            log.warn("Uso inválido de la API de acceso a datos al ordenar: {}", ex.getMessage());
            throw new OrdenamientoInvalidoException(
                    pageable.getSort().toString(), ex);
        }
    }
}
