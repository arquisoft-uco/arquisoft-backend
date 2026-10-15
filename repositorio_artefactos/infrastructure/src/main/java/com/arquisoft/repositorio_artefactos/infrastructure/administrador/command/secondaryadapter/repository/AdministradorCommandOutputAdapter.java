package com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.administrador.command.secondaryadapter.mapper.AdministradorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.AdministradorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdministradorCommandOutputAdapter implements AdministradorOutputPort {

    private final AdministradorCommandRepository administradorCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(AdministradorEntity administrador) {
        administradorCommandRepository.save(AdministradorJpaMapper.toJpaEntity(administrador));
        logger.debug(AdministradorKey.LOG_GUARDADO, administrador.id());
    }

    @Override
    public void eliminarLogica(UUID id, Instant ocurridoEn) {
        administradorCommandRepository.eliminarLogica(id, ocurridoEn);
        logger.debug(AdministradorKey.LOG_ACTUALIZADO, id);
    }

    @Override
    public void reactivar(AdministradorEntity administrador) {
        administradorCommandRepository.reactivar(administrador.id(), administrador.identificador(), administrador.nombre(),
                administrador.email(), administrador.ocurridoEn());
        logger.debug(AdministradorKey.LOG_ACTUALIZADO, administrador.id());
    }

    @Override
    public Optional<AdministradorEntity> obtenerPorId(UUID id) {
        return administradorCommandRepository.findById(id).map(AdministradorJpaMapper::toEntity);
    }

    @Override
    public void actualizar(AdministradorEntity administrador) {
        administradorCommandRepository.save(AdministradorJpaMapper.toJpaEntity(administrador));
        logger.debug(AdministradorKey.LOG_ACTUALIZADO, administrador.id());
    }
}
