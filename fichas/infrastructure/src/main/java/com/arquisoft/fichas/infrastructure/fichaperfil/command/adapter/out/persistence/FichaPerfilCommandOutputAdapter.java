package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.out.persistence;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
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
    private final CatalogoMensajes catalogo;

    @Override
    public void registrarFicha(FichaPerfilDomain ficha) {
        AsesorFichaEntity asesorRef = asesorFichaRepository.getReferenceById(ficha.getAsesorFicha());
        fichaPerfilRepository.save(FichaPerfilMapper.toEntity(ficha, asesorRef));
        logger.debug(catalogo.obtener(FichaPerfilKey.LOG_GUARDADA), ficha.getId());
    }

    @Override
    public void actualizarTitulo(UUID fichaPerfil, String tituloProyecto) {
        fichaPerfilRepository.actualizarTitulo(fichaPerfil, tituloProyecto);
        logger.debug(catalogo.obtener(FichaPerfilKey.LOG_GUARDADA), fichaPerfil);
    }

    @Override
    public void actualizarAsesor(UUID fichaPerfil, UUID nuevoAsesorFicha) {
        AsesorFichaEntity asesorRef = asesorFichaRepository.getReferenceById(nuevoAsesorFicha);
        fichaPerfilRepository.actualizarAsesorFicha(fichaPerfil, asesorRef);
        logger.debug(catalogo.obtener(FichaPerfilKey.LOG_GUARDADA), fichaPerfil);
    }

    @Override
    public Optional<FichaPerfilDomain> buscarPorId(UUID id) {
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

    @Override
    public boolean existeTituloEnOtraFicha(UUID fichaPerfil, String titulo) {
        return fichaPerfilRepository.existsByTituloProyectoAndIdNot(titulo, fichaPerfil);
    }
}
