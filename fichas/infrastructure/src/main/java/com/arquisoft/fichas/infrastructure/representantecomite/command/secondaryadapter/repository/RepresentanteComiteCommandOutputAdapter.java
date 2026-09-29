package com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.mapper.RepresentanteComiteJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
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
    public boolean existeVigentePorId(UUID id) {
        return representanteComiteCommandRepository.existsByIdAndEliminadoEnIsNull(id);
    }

    @Override
    public Optional<RepresentanteComiteEntity> obtenerPorId(UUID id) {
        return representanteComiteCommandRepository.findById(id).map(RepresentanteComiteJpaMapper::toEntity);
    }

    @Override
    public void guardar(RepresentanteComiteEntity representanteComite) {
        representanteComiteCommandRepository.save(RepresentanteComiteJpaMapper.toJpaEntity(representanteComite));
        logger.debug(RepresentanteComiteKey.LOG_GUARDADO, representanteComite.id());
    }

    @Override
    public void actualizar(RepresentanteComiteEntity representanteComite) {
        representanteComiteCommandRepository.save(RepresentanteComiteJpaMapper.toJpaEntity(representanteComite));
        logger.debug(RepresentanteComiteKey.LOG_ACTUALIZADO, representanteComite.id());
    }

    @Override
    public void reactivar(RepresentanteComiteEntity representanteComite) {
        representanteComiteCommandRepository.reactivar(representanteComite.id(), representanteComite.identificador(),
                representanteComite.nombre(), representanteComite.email(), representanteComite.ocurridoEn());
        logger.debug(RepresentanteComiteKey.LOG_ACTUALIZADO, representanteComite.id());
    }

    @Override
    public void eliminarLogica(UUID id, Instant ocurridoEn) {
        representanteComiteCommandRepository.eliminarLogica(id, ocurridoEn);
        logger.debug(RepresentanteComiteKey.LOG_ACTUALIZADO, id);
    }
}
