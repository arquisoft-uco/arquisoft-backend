package com.arquisoft.mapas_ruta.application.planestimado.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.command.finder.MapaRutaDeTareaFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.finder.DiasEstimadosAcumuladosDeMapaRutaFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.finder.PlanEstimadoDeTareaExisteFinder;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.PlanEstimadoOutputPort;
import com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.mapper.PlanEstimadoMapper;
import com.arquisoft.mapas_ruta.application.planestimado.command.usecase.AgregarPlanEstimadoUseCase;
import com.arquisoft.mapas_ruta.application.planestimado.command.validator.AgregarPlanEstimadoValidator;
import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarPlanEstimadoUseCaseImpl implements AgregarPlanEstimadoUseCase {

    private final PlanEstimadoOutputPort planEstimadoOutputPort;
    private final TareaPorIdFinder tareaPorIdFinder;
    private final MapaRutaDeTareaFinder mapaRutaDeTareaFinder;
    private final PlanEstimadoDeTareaExisteFinder planEstimadoDeTareaExisteFinder;
    private final DiasEstimadosAcumuladosDeMapaRutaFinder diasEstimadosAcumuladosDeMapaRutaFinder;
    private final AgregarPlanEstimadoValidator agregarPlanEstimadoValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(PlanEstimadoDomain entrada) {
        logger.info(PlanEstimadoKey.LOG_AGREGANDO, entrada.getTarea(), entrada.getTiempoEstimadoDias());

        var tarea = tareaPorIdFinder.obtener(entrada.getTarea());
        var mapaRuta = mapaRutaDeTareaFinder.obtener(entrada.getTarea());
        var planExiste = planEstimadoDeTareaExisteFinder.obtener(entrada.getTarea());
        var diasAcumulados = diasEstimadosAcumuladosDeMapaRutaFinder.obtener(entrada.getTarea());

        logger.debug(PlanEstimadoKey.LOG_VERIFICACION_AGREGAR,
                !tarea.esVacio(), !mapaRuta.esVacio(), planExiste, diasAcumulados);
        agregarPlanEstimadoValidator.validar(entrada, tarea, mapaRuta, planExiste, diasAcumulados);

        planEstimadoOutputPort.registrar(PlanEstimadoMapper.toEntity(entrada));

        logger.info(PlanEstimadoKey.LOG_AGREGADO, entrada.getId(), entrada.getTarea());
        return entrada.getId();
    }
}
