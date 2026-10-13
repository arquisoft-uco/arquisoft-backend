package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.mapper.AsesorFichaJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaCommandOutputAdapter implements AsesorFichaOutputPort {

    private final AsesorFichaCommandRepository asesorFichaCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(AsesorFichaEntity asesorFicha) {
        asesorFichaCommandRepository.save(AsesorFichaJpaMapper.toJpaEntity(asesorFicha));
        logger.debug(AgregarAsesorFichaKey.LOG_GUARDADO, asesorFicha.usuario());
    }

    @Override
    public void eliminarLogica(UUID usuario, Instant eliminadoEn) {
        asesorFichaCommandRepository.eliminarLogica(usuario, AsesorFichaJpaMapper.aColumna(eliminadoEn));
        logger.debug(AgregarAsesorFichaKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public void reactivar(UUID usuario) {
        asesorFichaCommandRepository.reactivar(usuario);
        logger.debug(AgregarAsesorFichaKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public Optional<AsesorFichaEntity> obtenerPorUsuario(UUID usuario) {
        return asesorFichaCommandRepository.findById(usuario).map(AsesorFichaJpaMapper::toEntity);
    }
}
