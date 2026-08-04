package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.out.persistence;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilMapper;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilRepository;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilCommandOutputAdapter implements FichaPerfilOutputPort {

    private final FichaPerfilRepository fichaPerfilRepository;
    private final AsesorFichaRepository asesorFichaRepository;
    private final EstudianteFichaPerfilRepository estudianteFichaPerfilRepository;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public void guardar(FichaPerfilAggregate ficha) {
        AsesorFichaEntity asesorRef =
                asesorFichaRepository.getReferenceById(ficha.getAsesorFicha());
        fichaPerfilRepository.save(FichaPerfilMapper.toEntity(ficha, asesorRef));
        logger.debug(catalog.obtener(FichasKeys.FichaPerfil.LOG_GUARDADA), ficha.getId());
    }

    @Override
    public Optional<FichaPerfilAggregate> buscarPorId(UUID id) {
        return fichaPerfilRepository.findById(id).map(FichaPerfilMapper::toDomain);
    }

    @Override
    public boolean existePorId(UUID id) {
        return fichaPerfilRepository.existsById(id);
    }

    @Override
    public boolean esEstudiantePropietario(PropietarioFichaCriteria criteria) {
        return estudianteFichaPerfilRepository.existsByFichaPerfilIdAndEstudianteId(
                criteria.fichaPerfil(), criteria.estudiante());
    }

    @Override
    public boolean existePorTituloProyecto(String titulo) {
        return fichaPerfilRepository.existsByTituloProyecto(titulo);
    }
}
