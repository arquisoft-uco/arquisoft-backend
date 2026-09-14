package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.ProyectoEstudianteAccesoOutputPort;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.ProyectoEstudianteAccesoEntity;
import com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.mapper.ProyectoEstudianteAccesoJpaMapper;
import com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.exception.ContactosEvaluacionNoDisponiblesException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ProyeccionAccesoEvaluacionKey;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoEstudianteAccesoCommandOutputAdapter implements ProyectoEstudianteAccesoOutputPort {

    private static final String SQL_ESTUDIANTES_CON_ACCESO = """
            SELECT pea.estudiante_id AS estudiante
            FROM entregable_proyecto_acceso epa
            JOIN proyecto_estudiante_acceso pea ON pea.proyecto_id = epa.proyecto_id AND pea.activo = true
            WHERE epa.entregable_id = :entregable AND epa.activo = true
            """;

    private final ProyectoEstudianteAccesoCommandRepository repository;
    private final AppLogger logger;

    @PersistenceContext(unitName = "evaluaciones")
    private EntityManager entityManager;

    @Override
    public Optional<ProyectoEstudianteAccesoEntity> buscarPorProyectoYEstudiante(UUID proyecto, UUID estudiante) {
        return repository.findByProyectoAndEstudiante(proyecto, estudiante)
                .map(ProyectoEstudianteAccesoJpaMapper::toEntity);
    }

    @Override
    public void guardar(ProyectoEstudianteAccesoEntity entity) {
        repository.save(ProyectoEstudianteAccesoJpaMapper.toJpaEntity(entity));
        logger.debug(ProyeccionAccesoEvaluacionKey.LOG_PROYECCION_ACTUALIZADA, entity.proyecto());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<UUID> obtenerEstudiantesConAccesoPorEntregable(UUID entregable) {
        List<UUID> filas = entityManager.createNativeQuery(SQL_ESTUDIANTES_CON_ACCESO)
                .setParameter("entregable", entregable)
                .getResultList();

        if (filas.isEmpty()) {
            throw new ContactosEvaluacionNoDisponiblesException();
        }

        return Set.copyOf(filas);
    }
}
