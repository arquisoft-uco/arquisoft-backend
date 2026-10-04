package com.arquisoft.mapas_ruta.infrastructure.asignacionproyecto.query.secondaryadapter.webclient;

import com.arquisoft.mapas_ruta.application.asignacionproyecto.query.secondaryport.ProyectoGradoDeEstudianteQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.AsignacionProyectoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

// STUB deliberado (HU-052): la impl real es una consulta sincrona a `proyectos` via `shared:web-client`
// (proyecto de grado del estudiante). Hoy no existen ni el modulo ni la consulta, asi que el adaptador
// ignora al estudiante y devuelve siempre el mismo proyecto fijo. El puerto, el finder y el corte del
// use case ya estan cableados: activarlo es reemplazar este cuerpo y borrar la constante.
// Ver PLAN-HU-052.md (seccion 5) y CLAUDE.md ("Desviaciones conocidas").
@Component
@RequiredArgsConstructor
public class ProyectoGradoDeEstudianteQueryOutputAdapter implements ProyectoGradoDeEstudianteQueryOutputPort {

    private static final UUID PROYECTO_GRADO_SIMULADO = UUID.fromString("7f5c3c0f-ec10-4662-bbf7-c89d135ffd7b");

    private final AppLogger logger;

    @Override
    public Optional<UUID> obtenerProyectoGrado(UUID estudiante) {
        logger.warn(AsignacionProyectoKey.LOG_PROYECTO_ESTUDIANTE_SIMULADO, estudiante, PROYECTO_GRADO_SIMULADO);
        return Optional.of(PROYECTO_GRADO_SIMULADO);
    }
}
