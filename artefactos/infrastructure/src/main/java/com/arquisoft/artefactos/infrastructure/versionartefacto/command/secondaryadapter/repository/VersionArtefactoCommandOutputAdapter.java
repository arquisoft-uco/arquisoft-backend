package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.mapper.VersionArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionArtefactoKey;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionArtefactoCommandOutputAdapter implements VersionArtefactoOutputPort {

    private final VersionArtefactoCommandRepository versionArtefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(VersionArtefactoEntity version) {
        versionArtefactoCommandRepository.save(VersionArtefactoJpaMapper.toJpaEntity(version));
        logger.debug(VersionArtefactoKey.LOG_GUARDADA, version.id());
    }

    @Override
    public int ultimoNumeroPorArtefacto(UUID artefacto) {
        return versionArtefactoCommandRepository.obtenerUltimoNumero(artefacto);
    }

    @Override
    public Optional<VersionArtefactoEntity> obtenerPorId(UUID id) {
        return versionArtefactoCommandRepository.findById(id).map(VersionArtefactoJpaMapper::toEntity);
    }

    @Override
    public boolean esUltimaDeSuArtefacto(UUID versionArtefacto) {
        return versionArtefactoCommandRepository.esUltimaDeSuArtefacto(versionArtefacto);
    }

    @Override
    public Optional<VersionArtefactoEntity> obtenerBloqueadaPorId(UUID id) {
        return versionArtefactoCommandRepository.bloquearPorId(id).map(VersionArtefactoJpaMapper::toEntity);
    }

    @Override
    public boolean existeParticipacion(UUID versionArtefacto, UUID estudiante) {
        return versionArtefactoCommandRepository.existeParticipacion(versionArtefacto, estudiante);
    }

    @Override
    public void eliminar(UUID versionArtefacto) {
        versionArtefactoCommandRepository.deleteById(versionArtefacto);
        logger.debug(EliminarVersionArtefactoKey.LOG_ELIMINADO, versionArtefacto);
    }

    @Override
    public long tamanoVigenteDelProyectoDeArtefacto(UUID artefacto, String estadoCerrada) {
        return versionArtefactoCommandRepository.tamanoVigenteDelProyectoDeArtefacto(artefacto, estadoCerrada);
    }

    @Override
    public long tamanoVigenteDelArtefacto(UUID artefacto, String estadoCerrada) {
        return versionArtefactoCommandRepository.tamanoVigenteDelArtefacto(artefacto, estadoCerrada);
    }

    @Override
    public List<String> obtenerUrisArchivoPorProyectoGrado(UUID proyectoGrado) {
        return versionArtefactoCommandRepository.obtenerUrisArchivoPorProyectoGrado(proyectoGrado);
    }
}
