package com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.entregables.infrastructure.artefacto.command.secondaryadapter.mapper.ArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactoCommandOutputAdapter implements ArtefactoOutputPort {

    private final ArtefactoCommandRepository artefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrarVersionCerrada(ArtefactoEntity artefacto) {
        var jpa = ArtefactoJpaMapper.toJpaEntity(artefacto);
        if (artefactoCommandRepository.existsById(artefacto.id())) {
            var filas = artefactoCommandRepository.avanzarVersion(jpa);
            logger.debug(ArtefactoKey.LOG_GUARDADO, artefacto.id(), filas);
            return;
        }
        artefactoCommandRepository.save(jpa);
        logger.debug(ArtefactoKey.LOG_GUARDADO, artefacto.id(), 1);
    }

    @Override
    public void marcarOmision(UUID id, Instant omitidoEn, Instant ocurridoEn) {
        var filas = artefactoCommandRepository.marcarOmision(id, omitidoEn, ocurridoEn);
        logger.debug(ArtefactoKey.LOG_OMISION_ACTUALIZADA, id, filas);
    }

    @Override
    public void limpiarOmision(UUID id, Instant ocurridoEn) {
        var filas = artefactoCommandRepository.limpiarOmision(id, ocurridoEn);
        logger.debug(ArtefactoKey.LOG_OMISION_ACTUALIZADA, id, filas);
    }

    @Override
    public Optional<ArtefactoEntity> obtenerPorId(UUID id) {
        return artefactoCommandRepository.findById(id).map(ArtefactoJpaMapper::toEntity);
    }

    @Override
    public List<ArtefactoEntity> obtenerVigentesPorProyecto(UUID proyectoGrado) {
        return artefactoCommandRepository
                .findByProyectoGradoAndOmitidoEnIsNullOrderByNombreDocumentoAscIdAsc(proyectoGrado).stream()
                .map(ArtefactoJpaMapper::toEntity)
                .toList();
    }

    @Override
    public List<String> obtenerClavesPorProyecto(UUID proyectoGrado) {
        return artefactoCommandRepository.buscarClavesPorProyecto(proyectoGrado);
    }
}
