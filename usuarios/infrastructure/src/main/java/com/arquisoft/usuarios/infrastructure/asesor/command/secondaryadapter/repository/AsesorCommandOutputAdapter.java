package com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.mapper.AsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorCommandOutputAdapter implements AsesorOutputPort {

    private final AsesorCommandRepository asesorCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(AsesorEntity asesor) {
        asesorCommandRepository.save(AsesorJpaMapper.toJpaEntity(asesor));
        logger.debug(AgregarAsesorKey.LOG_GUARDADO, asesor.usuario());
    }

    @Override
    public boolean existePorUsuario(UUID usuario) {
        return asesorCommandRepository.existsById(usuario);
    }
}
