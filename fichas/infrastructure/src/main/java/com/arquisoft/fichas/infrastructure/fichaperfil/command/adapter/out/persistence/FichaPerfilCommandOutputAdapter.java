package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FichaPerfilCommandOutputAdapter implements FichaPerfilOutputPort {

    private final FichaPerfilJpaRepository fichaPerfilJpaRepository;

    @PersistenceContext(unitName = "fichas")
    private EntityManager entityManager;

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
}
