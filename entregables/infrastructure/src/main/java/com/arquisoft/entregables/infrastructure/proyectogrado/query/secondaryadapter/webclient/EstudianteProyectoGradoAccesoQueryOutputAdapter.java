package com.arquisoft.entregables.infrastructure.proyectogrado.query.secondaryadapter.webclient;

import com.arquisoft.entregables.application.proyectogrado.query.secondaryport.EstudianteProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

// STUB deliberado (HU-049): la impl real es una consulta sincrona a `proyectos` via `shared:web-client`
// (¿el estudiante esta asignado al proyecto de grado?). Hoy no existen ni el modulo ni la consulta, asi que
// el adaptador ignora los argumentos y responde siempre que el estudiante pertenece. El puerto, el finder y
// la regla ya estan cableados: activarlo es reemplazar este cuerpo por la consulta a `proyectos` (reenviando
// el bearer del llamante; un fallo de transporte sale como InfrastructureException 503) y borrar el warn.
// Ver PLAN-HU-049.md (seccion 5) y CLAUDE.md ("Desviaciones conocidas").
@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoAccesoQueryOutputAdapter implements EstudianteProyectoGradoAccesoQueryOutputPort {

    private final AppLogger logger;

    @Override
    public boolean perteneceAlProyecto(UUID estudiante, UUID proyectoGrado) {
        logger.warn(ProyectoGradoKey.LOG_PERTENENCIA_SIMULADA, estudiante, proyectoGrado);
        return true;
    }
}
