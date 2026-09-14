package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.webclient;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.ContactoEstudianteOutputPort;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.DestinatarioEvaluacionEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ProyeccionAccesoEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// STUB — TODO(HU-215): la consulta real es sincrona a `usuarios` (email por usuario) via
// `shared:web-client`. Hoy no existen ni el modulo ni la query, asi que el adaptador aprueba
// siempre (devuelve un contacto fabricado por cada estudiante solicitado) y
// `ContactosEvaluacionNoDisponiblesException` no dispara todavia. El puerto y el Finder ya
// estan cableados: activarlo es reemplazar este cuerpo. Ver PLAN-HU-215.md y CLAUDE.md
// ("Consultas sincronas entre contextos" / "Desviaciones conocidas").
@Component
@RequiredArgsConstructor
public class ContactoEstudianteOutputAdapter implements ContactoEstudianteOutputPort {

    // El UUID (36) + dominio deben caber en NotificacionesLimits.Notificacion.DESTINATARIO_MAX (50):
    // un email fabricado mas largo que el limite real de usuarios.email (tambien 50) violaria una
    // restriccion que ningun contacto real de "usuarios" podria violar.
    private static final String DOMINIO_STUB = "@stub.local";

    private final AppLogger logger;

    @Override
    public List<DestinatarioEvaluacionEntity> consultarContactos(Set<UUID> estudiantes) {
        logger.warn(ProyeccionAccesoEvaluacionKey.LOG_CONTACTOS_NO_VERIFICADOS, estudiantes.size());

        return estudiantes.stream()
                .map(estudiante -> new DestinatarioEvaluacionEntity(estudiante, estudiante + DOMINIO_STUB))
                .toList();
    }
}
