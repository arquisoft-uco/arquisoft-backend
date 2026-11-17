package com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.AutorDeEntregableEntity;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper.EstudianteProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoCommandOutputAdapter implements EstudianteProyectoGradoOutputPort {

    private final EstudianteProyectoGradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public List<EstudianteProyectoGradoEntity> consultarPorProyectoGrado(UUID proyectoGrado) {
        return repository.findByProyectoGradoId(proyectoGrado).stream()
                .map(EstudianteProyectoGradoJpaMapper::toEntity)
                .toList();
    }

    @Override
    public Optional<EstudianteProyectoGradoEntity> obtenerPorAutorYProyectoGrado(UUID autor, UUID proyectoGrado) {
        return repository.findByAutorIdAndProyectoGradoId(autor, proyectoGrado)
                .map(EstudianteProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public List<AutorDeEntregableEntity> consultarAutoresPorEntregable(UUID entregableProyectoGrado) {
        return repository.consultarAutoresPorEntregable(entregableProyectoGrado).stream()
                .map(fila -> new AutorDeEntregableEntity(fila.autor(), fila.vigente()))
                .toList();
    }

    @Override
    public void guardarTodos(List<EstudianteProyectoGradoEntity> vinculos) {
        repository.saveAll(vinculos.stream().map(EstudianteProyectoGradoJpaMapper::toJpaEntity).toList());
        logger.debug(EstudianteProyectoGradoKey.LOG_GUARDADO, vinculos.size());
    }

    @Override
    public void guardar(EstudianteProyectoGradoEntity vinculo) {
        repository.save(EstudianteProyectoGradoJpaMapper.toJpaEntity(vinculo));
        logger.debug(EstudianteProyectoGradoKey.LOG_GUARDADO, 1);
    }
}
