package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.mapper.AsesorProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorProyectoGradoCommandOutputAdapter implements AsesorProyectoGradoOutputPort {

    private final AsesorProyectoGradoCommandRepository asesorProyectoGradoRepository;
    private final AppLogger logger;

    @Override
    public void registrar(AsesorProyectoGradoEntity vinculo) {
        asesorProyectoGradoRepository.save(AsesorProyectoGradoJpaMapper.toJpaEntity(vinculo));
        logger.debug(AsesorProyectoGradoKey.LOG_GUARDADO, vinculo.id(), vinculo.proyectoGrado());
    }

    @Override
    public boolean existePorAsesorYProyectoGradoConEstadoVigente(UUID asesor, UUID proyectoGrado,
                                                                 String estadoAsesor) {
        return asesorProyectoGradoRepository
                .existsByAsesorAndProyectoGradoWithEstadoVigente(asesor, proyectoGrado, estadoAsesor);
    }

    @Override
    public long contarPorProyectoGradoConEstadoVigente(UUID proyectoGrado, String estadoAsesor) {
        return asesorProyectoGradoRepository.countByProyectoGradoWithEstadoVigente(proyectoGrado, estadoAsesor);
    }

    @Override
    public Optional<AsesorProyectoGradoEntity> obtenerVigentePorAsesorYProyectoGrado(UUID asesor,
                                                                                    UUID proyectoGrado) {
        return asesorProyectoGradoRepository
                .findFirstByAsesorIdAndProyectoGradoIdOrderByFechaAsignadoDesc(asesor, proyectoGrado)
                .map(AsesorProyectoGradoJpaMapper::toEntity);
    }
}
