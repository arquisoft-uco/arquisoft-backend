package com.arquisoft.solicitudes.infrastructure.asignacionproyecto.command.secondaryadapter.webclient;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.SolicitudKey;
import com.arquisoft.solicitudes.application.asignacionproyecto.command.secondaryport.AsignacionProyectoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

// STUB — TODO(HU-081): la comprobacion real es una consulta sincrona a `proyectos`
// (proyecto_grado.coordinador_id / asesor_proyecto_grado) via `shared:web-client`. Hoy no existen
// ni el modulo ni la query, asi que el adaptador aprueba siempre y `DestinatarioAsignadoRule`
// no rechaza nada todavia. La regla, el puerto y el finder ya estan cableados: activarlo es
// reemplazar este cuerpo. Ver PLAN-HU-081.md ("Fuera de alcance") y CLAUDE.md
// ("Consultas sincronas entre contextos" / "Desviaciones conocidas").
@Component
@RequiredArgsConstructor
public class AsignacionProyectoOutputAdapter implements AsignacionProyectoOutputPort {

    private final AppLogger logger;

    @Override
    public boolean esCoordinadorAsignado(UUID estudianteUsuario, UUID coordinadorUsuario) {
        logger.warn(SolicitudKey.LOG_ASIGNACION_NO_VERIFICADA, coordinadorUsuario, estudianteUsuario);
        return true;
    }
}
