package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.mapper.VersionRepositorioArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AgregarVersionRepositorioArtefactoKey;
import com.arquisoft.shared.message.key.artefactos.CambiarNombreRepositorioVersionRepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionRepositorioArtefactoCommandOutputAdapter implements VersionRepositorioArtefactoOutputPort {

    private final VersionRepositorioArtefactoCommandRepository versionRepositorioArtefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(VersionRepositorioArtefactoEntity version) {
        versionRepositorioArtefactoCommandRepository.save(VersionRepositorioArtefactoJpaMapper.toJpaEntity(version));
        logger.debug(AgregarVersionRepositorioArtefactoKey.LOG_GUARDADA, version.id());
    }

    @Override
    public boolean existePorId(UUID id) {
        return versionRepositorioArtefactoCommandRepository.existsById(id);
    }

    @Override
    public Optional<VersionRepositorioArtefactoEntity> obtenerPorId(UUID id) {
        return versionRepositorioArtefactoCommandRepository.findById(id)
                .map(VersionRepositorioArtefactoJpaMapper::toEntity);
    }

    @Override
    public Optional<VersionRepositorioArtefactoEntity> obtenerPorArtefacto(UUID artefacto) {
        return versionRepositorioArtefactoCommandRepository.buscarPorArtefacto(artefacto)
                .map(VersionRepositorioArtefactoJpaMapper::toEntity);
    }

    @Override
    public List<UUID> consultarRepositoriosAsignados(UUID proyectoGrado) {
        return versionRepositorioArtefactoCommandRepository.consultarRepositoriosAsignados(proyectoGrado);
    }

    @Override
    public void actualizarNombreRepositorio(UUID repositorioArtefacto, String nombreRepositorio) {
        versionRepositorioArtefactoCommandRepository.actualizarNombreRepositorio(
                repositorioArtefacto, nombreRepositorio);
        logger.debug(CambiarNombreRepositorioVersionRepositorioArtefactoKey.LOG_NOMBRE_ACTUALIZADO,
                repositorioArtefacto);
    }
}
