package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.usecase.impl;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.ProyectoEstudianteAccesoOutputPort;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.mapper.ProyectoEstudianteAccesoMapper;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.usecase.SincronizarProyectoEstudianteAccesoUseCase;
import com.arquisoft.evaluaciones.domain.proyectoestudianteacceso.ProyectoEstudianteAccesoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ProyeccionAccesoEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SincronizarProyectoEstudianteAccesoUseCaseImpl implements SincronizarProyectoEstudianteAccesoUseCase {

    private final ProyectoEstudianteAccesoOutputPort proyectoEstudianteAccesoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(ProyectoEstudianteAccesoDomain entrada) {
        var existente = proyectoEstudianteAccesoOutputPort
                .buscarPorProyectoYEstudiante(entrada.getProyecto(), entrada.getEstudiante())
                .map(ProyectoEstudianteAccesoMapper::toDomain)
                .orElse(ProyectoEstudianteAccesoDomain.VACIO);

        if (!entrada.esMasRecienteQue(existente)) {
            logger.debug(ProyeccionAccesoEvaluacionKey.LOG_EVENTO_ANTIGUO_DESCARTADO, entrada.getProyecto());
            return;
        }

        proyectoEstudianteAccesoOutputPort.guardar(ProyectoEstudianteAccesoMapper.toEntity(entrada));
    }
}
