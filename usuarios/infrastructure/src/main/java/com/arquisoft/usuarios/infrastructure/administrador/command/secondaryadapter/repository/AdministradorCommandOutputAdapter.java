package com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAdministradorKey;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.AdministradorOutputPort;
import com.arquisoft.usuarios.application.administrador.command.secondaryport.entity.AdministradorEntity;
import com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.mapper.AdministradorJpaMapper;
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
        logger.debug(AgregarAdministradorKey.LOG_GUARDADO, administrador.usuario());
    }

    @Override
    public void reactivar(UUID usuario) {
        administradorCommandRepository.reactivar(usuario);
        logger.debug(AgregarAdministradorKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public void eliminarLogica(UUID usuario, Instant eliminadoEn) {
        administradorCommandRepository.eliminarLogica(usuario, AdministradorJpaMapper.aColumna(eliminadoEn));
        logger.debug(AgregarAdministradorKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public Optional<AdministradorEntity> obtenerPorUsuario(UUID usuario) {
        return administradorCommandRepository.findById(usuario).map(AdministradorJpaMapper::toEntity);
    }

    @Override
    public long contarVigentes() {
        return administradorCommandRepository.countByEliminadoEnIsNull();
    }
}
