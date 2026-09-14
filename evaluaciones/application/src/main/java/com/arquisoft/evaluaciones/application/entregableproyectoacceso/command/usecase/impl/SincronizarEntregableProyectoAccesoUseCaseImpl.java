package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.usecase.impl;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.EntregableProyectoAccesoOutputPort;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.mapper.EntregableProyectoAccesoMapper;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.usecase.SincronizarEntregableProyectoAccesoUseCase;
import com.arquisoft.evaluaciones.domain.entregableproyectoacceso.EntregableProyectoAccesoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ProyeccionAccesoEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SincronizarEntregableProyectoAccesoUseCaseImpl implements SincronizarEntregableProyectoAccesoUseCase {

    private final EntregableProyectoAccesoOutputPort entregableProyectoAccesoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(EntregableProyectoAccesoDomain entrada) {
        var existente = entregableProyectoAccesoOutputPort.buscarPorEntregable(entrada.getEntregable())
                .map(EntregableProyectoAccesoMapper::toDomain)
                .orElse(EntregableProyectoAccesoDomain.VACIO);

        if (!entrada.esMasRecienteQue(existente)) {
            logger.debug(ProyeccionAccesoEvaluacionKey.LOG_EVENTO_ANTIGUO_DESCARTADO, entrada.getEntregable());
            return;
        }

        entregableProyectoAccesoOutputPort.guardar(EntregableProyectoAccesoMapper.toEntity(entrada));
    }
}
