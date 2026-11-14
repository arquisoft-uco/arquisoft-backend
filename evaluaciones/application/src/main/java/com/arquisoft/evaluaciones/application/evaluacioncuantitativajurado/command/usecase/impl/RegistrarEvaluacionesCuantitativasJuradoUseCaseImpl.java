package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.IniciarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.ItemsEvaluacionCuantitativaJuradoRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.model.ItemsPorEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.mapper.EvaluacionCuantitativaJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.RegistrarEvaluacionesCuantitativasJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.RegistrarEvaluacionesCuantitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoCompletaFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.MarcarEvaluacionJuradoEvaluadaUseCase;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ValoresMaximosItemsCuantitativosJuradoFinder;
import com.arquisoft.evaluaciones.domain.evaluacion.InicioEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.RegistroEvaluacionesCuantitativasJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.DisponibilidadEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaItemsCuantitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajesVsValoresMaximosJurado;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.CompletitudEvaluacionJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionesCuantitativasJuradoUseCaseImpl
        implements RegistrarEvaluacionesCuantitativasJuradoUseCase {

    private final ContextoRegistroEvaluacionJuradoFinder contextoRegistroEvaluacionJuradoFinder;
    private final ValoresMaximosItemsCuantitativosJuradoFinder valoresMaximosItemsCuantitativosJuradoFinder;
    private final ItemsEvaluacionCuantitativaJuradoRegistradosFinder itemsEvaluacionCuantitativaJuradoRegistradosFinder;
    private final RegistrarEvaluacionesCuantitativasJuradoValidator validator;
    private final EvaluacionCuantitativaJuradoOutputPort evaluacionCuantitativaJuradoOutputPort;
    private final IniciarEvaluacionUseCase iniciarEvaluacionUseCase;
    private final EvaluacionJuradoCompletaFinder evaluacionJuradoCompletaFinder;
    private final MarcarEvaluacionJuradoEvaluadaUseCase marcarEvaluacionJuradoEvaluadaUseCase;
    private final AppLogger logger;

    @Override
    public void ejecutar(RegistroEvaluacionesCuantitativasJuradoDomain registro) {
        var evaluacionJurado = registro.getEvaluacionJurado();

        logger.info(EvaluacionCuantitativaJuradoKey.LOG_REGISTRANDO_LOTE,
                evaluacionJurado, registro.getEvaluaciones().size());

        var itemsSolicitados = registro.getEvaluaciones().stream()
                .map(EvaluacionCuantitativaJuradoDomain::getItem)
                .collect(Collectors.toUnmodifiableSet());
        var puntajes = registro.getEvaluaciones().stream()
                .collect(Collectors.toUnmodifiableMap(
                        EvaluacionCuantitativaJuradoDomain::getItem,
                        EvaluacionCuantitativaJuradoDomain::getPuntaje));

        var contexto = contextoRegistroEvaluacionJuradoFinder.obtener(evaluacionJurado);
        var valoresMaximos = valoresMaximosItemsCuantitativosJuradoFinder.obtener(itemsSolicitados);
        var itemsRegistrados = itemsEvaluacionCuantitativaJuradoRegistradosFinder.obtener(
                new ItemsPorEvaluacionCuantitativaJurado(evaluacionJurado, itemsSolicitados));

        logger.debug(EvaluacionCuantitativaJuradoKey.LOG_VERIFICACION_LOTE,
                evaluacionJurado, !contexto.esVacio(), valoresMaximos.size(), itemsRegistrados.size());

        validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, !contexto.esVacio()),
                new PropiedadEvaluacionJurado(evaluacionJurado, contexto.getJurado(), registro.getJurado()),
                new ExistenciaItemsCuantitativosJurado(itemsSolicitados, valoresMaximos),
                new PuntajesVsValoresMaximosJurado(puntajes, valoresMaximos),
                new DisponibilidadEvaluacionesCuantitativasJurado(
                        evaluacionJurado, itemsSolicitados, itemsRegistrados));

        iniciarEvaluacionUseCase.ejecutar(InicioEvaluacionDomain.crear(
                contexto.getEvaluacion(), contexto.getEstado()));

        var entidades = registro.getEvaluaciones().stream()
                .map(EvaluacionCuantitativaJuradoMapper::toEntity)
                .toList();
        evaluacionCuantitativaJuradoOutputPort.registrarTodas(entidades);

        var completa = evaluacionJuradoCompletaFinder.obtener(evaluacionJurado);
        marcarEvaluacionJuradoEvaluadaUseCase.ejecutar(
                CompletitudEvaluacionJuradoDomain.crear(evaluacionJurado, completa));

        logger.info(EvaluacionCuantitativaJuradoKey.LOG_LOTE_REGISTRADO,
                evaluacionJurado, entidades.size());
    }
}
