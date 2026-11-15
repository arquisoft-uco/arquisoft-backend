package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.entregable.command.usecase.AgregarEntregableUseCase;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.EvaluacionDeEntregableExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.EvaluacionEntity;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.EvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.GeneracionEvaluacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarEvaluacionUseCaseImplTest {

    @Mock
    private EvaluacionOutputPort evaluacionOutputPort;

    @Mock
    private EvaluacionDeEntregableExisteFinder evaluacionDeEntregableExisteFinder;

    @Mock
    private AgregarEntregableUseCase agregarEntregableUseCase;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private GenerarEvaluacionUseCaseImpl useCase;

    private EntregableDomain entregable;
    private EvaluacionDomain evaluacion;
    private GeneracionEvaluacionDomain generacion;

    @BeforeEach
    void setUp() {
        entregable = EntregableDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Sistema de riego", 1);
        evaluacion = EvaluacionDomain.crear(entregable.getId());
        generacion = GeneracionEvaluacionDomain.crear(entregable, evaluacion);
    }

    @Test
    void debeAgregarProyeccionYRegistrarEvaluacionEnOrden_cuandoNoExiste() {
        // Arrange
        when(evaluacionDeEntregableExisteFinder.obtener(entregable.getId())).thenReturn(false);

        // Act
        var resultado = useCase.ejecutar(generacion);

        // Assert
        var orden = inOrder(evaluacionDeEntregableExisteFinder, logger, agregarEntregableUseCase,
                evaluacionOutputPort);
        orden.verify(evaluacionDeEntregableExisteFinder).obtener(entregable.getId());
        orden.verify(logger).debug(EvaluacionKey.LOG_VERIFICACION_GENERAR, entregable.getId(), false);
        orden.verify(agregarEntregableUseCase).ejecutar(entregable);
        var captor = ArgumentCaptor.forClass(EvaluacionEntity.class);
        orden.verify(evaluacionOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(evaluacion.getId());
        assertThat(captor.getValue().entregable()).isEqualTo(entregable.getId());
        assertThat(captor.getValue().estado()).isEqualTo("PENDIENTE");
        assertThat(resultado).isEqualTo(
                new GeneracionEvaluacionResult.Generada(evaluacion.getId(), entregable.getId()));
    }

    @Test
    void debeDevolverDuplicadaSinEscribir_cuandoYaExiste() {
        // Arrange
        when(evaluacionDeEntregableExisteFinder.obtener(entregable.getId())).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(generacion);

        // Assert
        assertThat(resultado).isEqualTo(new GeneracionEvaluacionResult.Duplicada(entregable.getId()));
        verifyNoInteractions(agregarEntregableUseCase, evaluacionOutputPort);
    }
}
