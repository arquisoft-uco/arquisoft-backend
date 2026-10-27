package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.secondaryadapter.mapper.AsesorProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorProyectoGradoCommandOutputAdapter implements AsesorProyectoGradoOutputPort {

    private final AsesorProyectoGradoCommandRepository asesorProyectoGradoCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<AsesorProyectoGradoEntity> obtenerPorAsesorYProyectoGrado(UUID asesor, UUID proyectoGrado) {
        return asesorProyectoGradoCommandRepository.findByAsesorIdAndProyectoGradoId(asesor, proyectoGrado)
                .map(AsesorProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<Instant> obtenerUltimoOcurridoEnPorProyectoGrado(UUID proyectoGrado) {
        return asesorProyectoGradoCommandRepository.findUltimoOcurridoEnByProyectoGradoId(proyectoGrado);
    }

    @Override
    public List<AsesorProyectoGradoEntity> obtenerMasRecientesPorProyectoGrado(UUID proyectoGrado) {
        return asesorProyectoGradoCommandRepository.findMasRecientesByProyectoGradoId(proyectoGrado).stream()
                .map(AsesorProyectoGradoJpaMapper::toEntity)
                .toList();
    }

    @Override
    public void guardar(AsesorProyectoGradoEntity vinculo) {
        asesorProyectoGradoCommandRepository.save(AsesorProyectoGradoJpaMapper.toJpaEntity(vinculo));
        logger.debug(AsesorProyectoGradoKey.LOG_GUARDADO, vinculo.id());
    }
}
