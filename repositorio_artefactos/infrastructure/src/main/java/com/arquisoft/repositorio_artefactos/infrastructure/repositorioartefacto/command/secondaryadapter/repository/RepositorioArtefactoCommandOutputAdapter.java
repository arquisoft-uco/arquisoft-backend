package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.mapper.RepositorioArtefactoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public boolean existePorNombreDescripcionYAdministrador(
            String nombreRepositorio, String descripcion, UUID administrador) {
        return repositorioArtefactoCommandRepository.existsByNombreRepositorioAndDescripcionAndAdministrador(
                nombreRepositorio, descripcion, administrador);
    }
}
