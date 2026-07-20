package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilMapper;
import com.arquisoft.shared.message.FichasMessages;
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
    private final AsesorFichaJpaRepository asesorFichaJpaRepository;

    @Override
    public void guardar(FichaPerfilAggregate ficha) {
        AsesorFichaJpaEntity asesorRef =
                asesorFichaJpaRepository.getReferenceById(ficha.getAsesorFichaId());
        fichaPerfilJpaRepository.save(FichaPerfilMapper.toEntity(ficha, asesorRef));
        log.debug(FichasMessages.FichaPerfil.LOG_GUARDADA, ficha.getId());
    }

    @Override
    public Optional<FichaPerfilAggregate> buscarPorId(UUID id) {
        return fichaPerfilJpaRepository.findById(id).map(FichaPerfilMapper::toDomain);
    }

    @Override
    public boolean existsByTituloProyecto(String titulo) {
        return fichaPerfilJpaRepository.existsByTituloProyecto(titulo);
    }
}
