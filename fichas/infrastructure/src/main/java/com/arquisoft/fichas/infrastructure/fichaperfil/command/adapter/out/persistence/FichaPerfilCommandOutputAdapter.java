package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilMapper;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilJpaRepository;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilCommandOutputAdapter implements FichaPerfilOutputPort {

    private final FichaPerfilJpaRepository fichaPerfilJpaRepository;
    private final AsesorFichaJpaRepository asesorFichaJpaRepository;
    private final EstudianteFichaPerfilJpaRepository estudianteFichaPerfilJpaRepository;
    private final AppLogger logger;

    @Override
    public void guardar(FichaPerfilAggregate ficha) {
        AsesorFichaJpaEntity asesorRef =
                asesorFichaJpaRepository.getReferenceById(ficha.getAsesorFichaId());
        fichaPerfilJpaRepository.save(FichaPerfilMapper.toEntity(ficha, asesorRef));
        logger.debug(FichasMessages.FichaPerfil.LOG_GUARDADA, ficha.getId());
    }

    @Override
    public Optional<FichaPerfilAggregate> buscarPorId(UUID id) {
        return fichaPerfilJpaRepository.findById(id).map(FichaPerfilMapper::toDomain);
    }

    @Override
    public boolean existePorId(UUID id) {
        return fichaPerfilJpaRepository.existsById(id);
    }

    @Override
    public boolean esEstudiantePropietario(PropietarioFichaCriteria criteria) {
        return estudianteFichaPerfilJpaRepository.existsByFichaPerfilIdAndEstudianteId(
                criteria.fichaPerfil(), criteria.estudiante());
    }

    @Override
    public boolean existePorTituloProyecto(String titulo) {
        return fichaPerfilJpaRepository.existsByTituloProyecto(titulo);
    }
}
