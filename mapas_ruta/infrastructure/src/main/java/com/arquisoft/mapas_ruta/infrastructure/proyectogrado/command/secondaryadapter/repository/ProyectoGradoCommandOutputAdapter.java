package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.secondaryadapter.mapper.ProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoMapasRutaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoCommandOutputAdapter implements ProyectoGradoOutputPort {

    private final ProyectoGradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void guardar(ProyectoGradoEntity proyecto) {
        repository.save(ProyectoGradoJpaMapper.toJpaEntity(proyecto));
        logger.debug(ProyectoGradoMapasRutaKey.LOG_GUARDADO, proyecto.id());
    }

    @Override
    public boolean existePorId(UUID proyectoGrado) {
        return repository.existsById(proyectoGrado);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerPorId(UUID proyectoGrado) {
        return repository.findById(proyectoGrado).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public void actualizarEstado(UUID proyectoGrado, String estadoProyecto, Instant ocurridoEn) {
        repository.actualizarEstado(proyectoGrado, estadoProyecto, ocurridoEn);
        logger.debug(ProyectoGradoMapasRutaKey.LOG_ESTADO_ACTUALIZADO, proyectoGrado, estadoProyecto, ocurridoEn);
    }
}
