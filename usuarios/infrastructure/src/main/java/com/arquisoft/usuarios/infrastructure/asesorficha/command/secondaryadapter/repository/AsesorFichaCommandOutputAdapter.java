package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.mapper.AsesorFichaJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarAsesorFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public boolean existePorUsuario(UUID usuario) {
        return asesorFichaCommandRepository.existsById(usuario);
    }
}
