package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.asesor.command.finder.AsesorVigenteExisteFinder;
import com.arquisoft.evaluaciones.application.entregable.command.usecase.AgregarEntregableUseCase;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.EvaluacionDeEntregableExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.EvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.validator.GenerarEvaluacionValidator;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.EvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.GeneracionEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.EvaluacionAsesorDomain;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarEvaluacionUseCaseImplTest {

    @Mock
    private EvaluacionOutputPort evaluacionOutputPort;

    @Mock
    private EvaluacionDeEntregableExisteFinder evaluacionDeEntregableExisteFinder;

    @Mock
    private EvaluacionAsesorOutputPort evaluacionAsesorOutputPort;

    @Mock
    private AsesorVigenteExisteFinder asesorVigenteExisteFinder;

    @Mock
    private GenerarEvaluacionValidator validator;

    @Mock
    private AgregarEntregableUseCase agregarEntregableUseCase;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private GenerarEvaluacionUseCaseImpl useCase;

    private EntregableDomain entregable;
    private EvaluacionDomain evaluacion;
    private EvaluacionAsesorDomain evaluacionAsesor;
    private GeneracionEvaluacionDomain generacion;

    @BeforeEach
    void setUp() {
        entregable = EntregableDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Sistema de riego", 1);
        evaluacion = EvaluacionDomain.crear(entregable.getId());
        evaluacionAsesor = EvaluacionAsesorDomain.crear(evaluacion.getId(), UtilUUID.generarNuevoUUID());
        generacion = GeneracionEvaluacionDomain.crear(entregable, evaluacion, evaluacionAsesor);
    }

    @Test
    void debeAgregarProyeccionYRegistrarEvaluacionEnOrden_cuandoNoExiste() {
        // Arrange
        when(evaluacionDeEntregableExisteFinder.obtener(entregable.getId())).thenReturn(false);
        when(asesorVigenteExisteFinder.obtener(evaluacionAsesor.getAsesor())).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(generacion);

        // Assert
        var orden = inOrder(evaluacionDeEntregableExisteFinder, logger, validator, agregarEntregableUseCase,
                evaluacionOutputPort, evaluacionAsesorOutputPort);
        orden.verify(evaluacionDeEntregableExisteFinder).obtener(entregable.getId());
        orden.verify(logger).debug(EvaluacionKey.LOG_VERIFICACION_GENERAR, entregable.getId(), false, true);
        orden.verify(validator).validar(new ExistenciaAsesor(evaluacionAsesor.getAsesor(), true));
        orden.verify(agregarEntregableUseCase).ejecutar(entregable);
        var captor = ArgumentCaptor.forClass(EvaluacionEntity.class);
        orden.verify(evaluacionOutputPort).registrar(captor.capture());
        var captorAsesor = ArgumentCaptor.forClass(EvaluacionAsesorEntity.class);
        orden.verify(evaluacionAsesorOutputPort).registrar(captorAsesor.capture());
        verify(evaluacionDeEntregableExisteFinder, times(1)).obtener(any());
        verify(asesorVigenteExisteFinder, times(1)).obtener(any());
        assertThat(captorAsesor.getValue().id()).isEqualTo(evaluacionAsesor.getId());
        assertThat(captorAsesor.getValue().evaluacion()).isEqualTo(evaluacion.getId());
        assertThat(captorAsesor.getValue().asesor()).isEqualTo(evaluacionAsesor.getAsesor());
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
        when(asesorVigenteExisteFinder.obtener(evaluacionAsesor.getAsesor())).thenReturn(true);

        // Act
        var resultado = useCase.ejecutar(generacion);

        // Assert
        assertThat(resultado).isEqualTo(new GeneracionEvaluacionResult.Duplicada(entregable.getId()));
        verifyNoInteractions(validator, agregarEntregableUseCase, evaluacionOutputPort, evaluacionAsesorOutputPort);
    }

    @Test
    void debePropagarRechazoYNoEscribir_cuandoElValidatorLanza() {
        // Arrange
        when(evaluacionDeEntregableExisteFinder.obtener(entregable.getId())).thenReturn(false);
        when(asesorVigenteExisteFinder.obtener(evaluacionAsesor.getAsesor())).thenReturn(false);
        doThrow(new AsesorNoEncontradoException(evaluacionAsesor.getAsesor()))
                .when(validator).validar(new ExistenciaAsesor(evaluacionAsesor.getAsesor(), false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(generacion))
                .isInstanceOf(AsesorNoEncontradoException.class);
        verify(agregarEntregableUseCase, never()).ejecutar(any());
        verify(evaluacionOutputPort, never()).registrar(any());
        verify(evaluacionAsesorOutputPort, never()).registrar(any());
    }
}
