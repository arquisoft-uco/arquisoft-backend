package com.arquisoft.fichas.infrastructure.adapter.out.persistence.fichaperfil;

import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.adapter.out.persistence.asesorficha.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.exception.OrdenamientoInvalidoException;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
import com.arquisoft.shared.web.util.PaginationMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FichaPerfilOutputAdapter implements FichaPerfilOutputPort, FichaPerfilQueryOutputPort {

    private final FichaPerfilJpaRepository fichaPerfilJpaRepository;
    private final EntityManager entityManager;

    @Override
    public void guardar(FichaPerfilAggregate ficha) {
        AsesorFichaJpaEntity asesorRef = entityManager.getReference(AsesorFichaJpaEntity.class, ficha.getAsesorFichaId());
        fichaPerfilJpaRepository.save(FichaPerfilMapper.toEntity(ficha, asesorRef));
        log.debug("FichaPerfil guardada: id={}", ficha.getId());
    }

    @Override
    public Optional<FichaPerfilAggregate> buscarPorId(UUID id) {
        return fichaPerfilJpaRepository.findById(id).map(FichaPerfilMapper::toDomain);
    }

    @Override
    public PaginatedResult<FichaPerfilReadModel> consultarTodas(PaginationRequest request) {
        Pageable pageable = PaginationMapper.toPageable(request);
        try {
            return PaginationMapper.toResult(
                    fichaPerfilJpaRepository.findAll(pageable)
                            .map(FichaPerfilMapper::toReadModel));
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
