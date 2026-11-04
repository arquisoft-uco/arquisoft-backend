package com.arquisoft.artefactos.application.versionartefacto.query.usecase.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoDetalleCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.AsesorDeVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.EstudianteParticipaVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.model.ParticipacionVersionArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoQueryOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.query.validator.ConsultarVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.versionartefacto.exception.ActorSinAccesoVersionArtefactoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarVersionArtefactoUseCaseImplTest {

    @Mock
    private AsesorDeVersionArtefactoQueryFinder asesorDeVersionArtefactoQueryFinder;

    @Mock
    private EstudianteParticipaVersionArtefactoQueryFinder estudianteParticipaVersionArtefactoQueryFinder;

    @Mock
    private ConsultarVersionArtefactoValidator consultarVersionArtefactoValidator;

    @Mock
    private VersionArtefactoQueryOutputPort versionArtefactoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarVersionArtefactoUseCaseImpl useCase;

    private final VersionArtefactoDetalleCriteria criteria = new VersionArtefactoDetalleCriteria(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true, true);

    @Test
    void debeConsultar_cuandoValidatorAprueba() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var participacion = new ParticipacionVersionArtefacto(criteria.versionArtefacto(), criteria.actor());
        var esperado = mock(VersionArtefactoReadModel.class);
        when(asesorDeVersionArtefactoQueryFinder.obtener(criteria.versionArtefacto())).thenReturn(asesor);
        when(estudianteParticipaVersionArtefactoQueryFinder.obtener(participacion)).thenReturn(true);
        when(versionArtefactoQueryOutputPort.consultarPorId(criteria.versionArtefacto())).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(asesorDeVersionArtefactoQueryFinder, estudianteParticipaVersionArtefactoQueryFinder,
                consultarVersionArtefactoValidator, versionArtefactoQueryOutputPort);
        orden.verify(asesorDeVersionArtefactoQueryFinder, times(1)).obtener(criteria.versionArtefacto());
        orden.verify(estudianteParticipaVersionArtefactoQueryFinder, times(1)).obtener(participacion);
        orden.verify(consultarVersionArtefactoValidator).validar(criteria, asesor, true);
        orden.verify(versionArtefactoQueryOutputPort).consultarPorId(criteria.versionArtefacto());
        verify(logger).debug(eq(VersionArtefactoKey.LOG_CONSULTA_DETALLE_COMPLETADA),
                eq(criteria.versionArtefacto()));
    }

    @Test
    void debePropagarRechazo_cuandoValidatorLanza() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        when(asesorDeVersionArtefactoQueryFinder.obtener(criteria.versionArtefacto())).thenReturn(asesor);
        when(estudianteParticipaVersionArtefactoQueryFinder.obtener(any())).thenReturn(false);
        doThrow(new ActorSinAccesoVersionArtefactoException(criteria.actor(), criteria.versionArtefacto()))
                .when(consultarVersionArtefactoValidator).validar(criteria, asesor, false);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(ActorSinAccesoVersionArtefactoException.class);
        verify(versionArtefactoQueryOutputPort, never()).consultarPorId(any());
        verify(logger, never()).debug(eq(VersionArtefactoKey.LOG_CONSULTA_DETALLE_COMPLETADA),
                eq(criteria.versionArtefacto()));
    }
}
