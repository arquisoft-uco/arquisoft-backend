package com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.proyectogrado.command.secondaryadapter.mapper.ProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoCommandOutputAdapter implements ProyectoGradoOutputPort {

    private final ProyectoGradoCommandRepository proyectoGradoRepository;
    private final AppLogger logger;

    @Override
    public void registrar(ProyectoGradoEntity proyectoGrado) {
        proyectoGradoRepository.save(ProyectoGradoJpaMapper.toJpaEntity(proyectoGrado));
        logger.debug(ProyectoGradoKey.LOG_GUARDADO, proyectoGrado.id(), proyectoGrado.fichaPerfil());
    }

    @Override
    public boolean existePorFichaPerfil(UUID fichaPerfil) {
        return proyectoGradoRepository.existsByFichaPerfilId(fichaPerfil);
    }

    @Override
    public Optional<ProyectoGradoEntity> obtenerPorId(UUID proyectoGrado) {
        return proyectoGradoRepository.findById(proyectoGrado).map(ProyectoGradoJpaMapper::toEntity);
    }
}
