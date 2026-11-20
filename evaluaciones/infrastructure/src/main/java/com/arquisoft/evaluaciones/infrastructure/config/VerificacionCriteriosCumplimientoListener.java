package com.arquisoft.evaluaciones.infrastructure.config;

import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.command.secondaryadapter.repository.CriterioItemCualitativoAsesorCommandRepository;
import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.command.secondaryadapter.repository.CriterioItemCualitativoJuradoCommandRepository;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.EvaluacionesCriterios;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VerificacionCriteriosCumplimientoListener {

    private static final String CATALOGO_JURADO = "criterio_item_cualitativo_jurado";
    private static final String CATALOGO_ASESOR = "criterio_item_cualitativo_asesor";

    private final CriterioItemCualitativoJuradoCommandRepository criterioJuradoRepository;
    private final CriterioItemCualitativoAsesorCommandRepository criterioAsesorRepository;
    private final AppLogger appLogger;

    @EventListener(ApplicationReadyEvent.class)
    public void verificar() {
        verificarCatalogo(CATALOGO_JURADO, criterioJuradoRepository::existePorNombreNormalizado);
        verificarCatalogo(CATALOGO_ASESOR, criterioAsesorRepository::existePorNombreNormalizado);
    }

    private void verificarCatalogo(String catalogo, Predicate<String> existe) {
        List.of(EvaluacionesCriterios.CUMPLE, EvaluacionesCriterios.NO_CUMPLE).stream()
                .filter(existe.negate())
                .forEach(nombre -> appLogger.warn(EvaluacionKey.LOG_CRITERIO_CUMPLIMIENTO_AUSENTE, catalogo, nombre));
    }
}
