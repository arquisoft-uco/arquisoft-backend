package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.MapaRutaOutputPort;
import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.mapper.MapaRutaJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MapaRutaCommandOutputAdapter implements MapaRutaOutputPort {

    private final MapaRutaCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(MapaRutaEntity entity) {
        repository.save(MapaRutaJpaMapper.toJpaEntity(entity));
        logger.debug(MapaRutaKey.LOG_GUARDADO, entity.id());
    }

    @Override
    public boolean existePorProyectoGrado(UUID proyectoGrado) {
        return repository.existsByProyectoGradoId(proyectoGrado);
    }

    @Override
    public Optional<MapaRutaEntity> obtenerPorId(UUID mapaRuta) {
        return repository.findById(mapaRuta).map(MapaRutaJpaMapper::toEntity);
    }

    @Override
    public void actualizarFechaFin(UUID mapaRuta, LocalDate fechaFin) {
        repository.actualizarFechaFin(mapaRuta, fechaFin);
        logger.debug(MapaRutaKey.LOG_GUARDADO, mapaRuta);
    }
}
