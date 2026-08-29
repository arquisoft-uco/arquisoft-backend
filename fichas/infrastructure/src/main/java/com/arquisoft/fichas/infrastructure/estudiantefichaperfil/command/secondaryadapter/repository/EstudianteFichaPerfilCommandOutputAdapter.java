package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.mapper.EstudianteFichaPerfilJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteFichaPerfilCommandOutputAdapter implements EstudianteFichaPerfilOutputPort {

    private final EstudianteFichaPerfilCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void vincularEstudiante(EstudianteFichaPerfilEntity relacion) {
        repository.save(EstudianteFichaPerfilJpaMapper.toJpaEntity(relacion));
        logger.debug(Mensajes.obtener(EstudianteFichaPerfilKey.LOG_VINCULO_GUARDADO),
                relacion.fichaPerfilId(), relacion.estudianteId());
    }

    @Override
    public boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId) {
        return repository.existsByFichaPerfilIdAndEstudianteId(fichaPerfilId, estudianteId);
    }

    @Override
    public long contarPorFichaPerfilId(UUID fichaPerfilId) {
        return repository.countByFichaPerfilId(fichaPerfilId);
    }

    @Override
    public void desvincularEstudiante(UUID fichaPerfilId, UUID estudianteId) {
        repository.deleteByFichaPerfilIdAndEstudianteId(fichaPerfilId, estudianteId);
        logger.debug(Mensajes.obtener(EstudianteFichaPerfilKey.LOG_VINCULO_ELIMINADO),
                fichaPerfilId, estudianteId);
    }
}
