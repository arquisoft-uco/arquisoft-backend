package com.arquisoft.evaluaciones.infrastructure.estudiantesproyectogrado.query.secondaryadapter.webclient;

import com.arquisoft.evaluaciones.application.estudiantesproyectogrado.query.secondaryport.EstudiantesProyectoGradoOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EstudiantesProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

// STUB — TODO(HU-016): la lista real de estudiantes de un proyecto de grado es una consulta
// sincrona a `proyectos` via `shared:web-client`. Hoy no existen ni el modulo ni la query, asi que
// el adaptador devuelve un roster fijo para cualquier proyecto — cargar manualmente uno de estos
// estudiantes en el proyecto de un `entregable` permite probar tanto pertenece=true como
// pertenece=false. El puerto y el Finder ya estan cableados; activarlo es reemplazar este cuerpo.
// Ver CLAUDE.md ("Consultas sincronas entre contextos" / "Desviaciones conocidas").
@Component
@RequiredArgsConstructor
public class EstudiantesProyectoGradoOutputAdapter implements EstudiantesProyectoGradoOutputPort {

    private static final List<UUID> ROSTER_FIJO = List.of(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            UUID.fromString("22222222-2222-2222-2222-222222222222"));

    private final AppLogger logger;

    @Override
    public List<UUID> obtenerEstudiantes(String proyecto) {
        logger.warn(EstudiantesProyectoGradoKey.LOG_ESTUDIANTES_NO_VERIFICADOS, proyecto);
        return ROSTER_FIJO;
    }
}
