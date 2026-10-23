package com.arquisoft.mapas_ruta.infrastructure.asignacionasesor.command.secondaryadapter.webclient;

import com.arquisoft.mapas_ruta.application.asignacionasesor.command.secondaryport.AsignacionAsesorOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.AsignacionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

// STUB deliberado (HU-007): la impl real es una consulta sincrona a `proyectos` via `shared:web-client`
// (asesores vigentes del proyecto de grado). Hoy no existen ni el modulo ni la consulta, asi que el
// adaptador ignora la asignacion y responde siempre que el asesor esta asignado. El puerto, el finder
// y la regla ya estan cableados: activarlo es reemplazar este cuerpo y borrar el warn.
// Ver PLAN-HU-007.md (seccion 5) y CLAUDE.md ("Desviaciones conocidas").
@Component
@RequiredArgsConstructor
public class AsignacionAsesorOutputAdapter implements AsignacionAsesorOutputPort {

    private final AppLogger logger;

    @Override
    public boolean estaAsignado(UUID proyectoGrado, UUID asesor) {
        logger.warn(AsignacionAsesorKey.LOG_ASESOR_ASIGNADO_SIMULADO, asesor, proyectoGrado);
        return true;
    }
}
