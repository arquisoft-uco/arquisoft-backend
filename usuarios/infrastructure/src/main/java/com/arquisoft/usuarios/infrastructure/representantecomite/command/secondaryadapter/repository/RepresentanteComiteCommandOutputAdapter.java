package com.arquisoft.usuarios.infrastructure.representantecomite.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarRepresentanteComiteKey;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.usuarios.infrastructure.representantecomite.command.secondaryadapter.mapper.RepresentanteComiteJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComiteCommandOutputAdapter implements RepresentanteComiteOutputPort {

    private final RepresentanteComiteCommandRepository representanteComiteCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(RepresentanteComiteEntity representanteComite) {
        representanteComiteCommandRepository.save(RepresentanteComiteJpaMapper.toJpaEntity(representanteComite));
        logger.debug(AgregarRepresentanteComiteKey.LOG_GUARDADO, representanteComite.usuario());
    }

    @Override
    public void eliminarLogica(UUID usuario, Instant eliminadoEn) {
        representanteComiteCommandRepository.eliminarLogica(usuario, RepresentanteComiteJpaMapper.aColumna(eliminadoEn));
        logger.debug(AgregarRepresentanteComiteKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public void reactivar(UUID usuario) {
        representanteComiteCommandRepository.reactivar(usuario);
        logger.debug(AgregarRepresentanteComiteKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public Optional<RepresentanteComiteEntity> obtenerPorUsuario(UUID usuario) {
        return representanteComiteCommandRepository.findById(usuario).map(RepresentanteComiteJpaMapper::toEntity);
    }
}
