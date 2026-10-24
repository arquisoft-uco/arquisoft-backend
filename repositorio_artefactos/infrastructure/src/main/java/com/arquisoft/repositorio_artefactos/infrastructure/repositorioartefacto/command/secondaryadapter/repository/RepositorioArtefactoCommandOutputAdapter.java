package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.mapper.RepositorioArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepositorioArtefactoCommandOutputAdapter implements RepositorioArtefactoOutputPort {

    private final RepositorioArtefactoCommandRepository repositorioArtefactoCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(RepositorioArtefactoEntity repositorio) {
        repositorioArtefactoCommandRepository.save(RepositorioArtefactoJpaMapper.toJpaEntity(repositorio));
        logger.debug(RepositorioArtefactoKey.LOG_GUARDADO, repositorio.id());
    }

    @Override
    public Optional<RepositorioArtefactoEntity> obtenerPorId(UUID id) {
        return repositorioArtefactoCommandRepository.findById(id).map(RepositorioArtefactoJpaMapper::toEntity);
    }

    @Override
    public boolean existePorNombreRepositorio(String nombreRepositorio) {
        return repositorioArtefactoCommandRepository.existsByNombreRepositorio(nombreRepositorio);
    }

    @Override
    public boolean existeOtroConNombreRepositorio(String nombreRepositorio, UUID repositorio) {
        return repositorioArtefactoCommandRepository.existsByNombreRepositorioAndIdNot(nombreRepositorio, repositorio);
    }

    @Override
    public void actualizarDatos(UUID repositorio, String nombreRepositorio, String descripcion) {
        repositorioArtefactoCommandRepository.actualizarDatos(repositorio, nombreRepositorio, descripcion);
        logger.debug(RepositorioArtefactoKey.LOG_ACTUALIZADO, repositorio);
    }
}
