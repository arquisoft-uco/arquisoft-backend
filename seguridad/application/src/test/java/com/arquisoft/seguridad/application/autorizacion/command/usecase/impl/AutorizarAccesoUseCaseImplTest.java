package com.arquisoft.seguridad.application.autorizacion.command.usecase.impl;

import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.DecisionAccesoOutputPort;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.domain.autorizacion.AccesoDomain;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizarAccesoUseCaseImplTest {

    @Mock
    private DecisionAccesoOutputPort decisionAccesoOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AutorizarAccesoUseCaseImpl useCase;

    private AccesoDomain acceso;
    private SolicitudAcceso solicitud;

    @BeforeEach
    void preparar() {
        acceso = AccesoDomain.crear("GET", "/api/x", "token", "principal", "corr");
        solicitud = new SolicitudAcceso("GET", "/api/x", "token", "principal", "corr");
    }

    @Test
    void debeRetornarPermitidaConIdDecision_cuandoPuertoPermite() {
        // Arrange
        when(decisionAccesoOutputPort.decidir(solicitud)).thenReturn(new DecisionAcceso.Permitida("dec-1"));

        // Act
        var resultado = useCase.ejecutar(acceso);

        // Assert
        assertThat(resultado).isEqualTo(new DecisionAccesoResult.Permitida("dec-1"));
        verify(decisionAccesoOutputPort, times(1)).decidir(solicitud);
    }

    @Test
    void debeRetornarNoAutenticada_cuandoPuertoLaRechaza() {
        // Arrange
        when(decisionAccesoOutputPort.decidir(solicitud)).thenReturn(new DecisionAcceso.NoAutenticada());

        // Act
        var resultado = useCase.ejecutar(acceso);

        // Assert
        assertThat(resultado).isInstanceOf(DecisionAccesoResult.NoAutenticada.class);
    }

    @Test
    void debeRetornarDenegada_cuandoPuertoDeniega() {
        // Arrange
        when(decisionAccesoOutputPort.decidir(solicitud)).thenReturn(new DecisionAcceso.Denegada());

        // Act
        var resultado = useCase.ejecutar(acceso);

        // Assert
        assertThat(resultado).isInstanceOf(DecisionAccesoResult.Denegada.class);
    }

    @Test
    void debePropagarExcepcion_cuandoPuertoFalla() {
        // Arrange
        when(decisionAccesoOutputPort.decidir(solicitud)).thenThrow(new IllegalStateException("caido"));

        // Act / Assert
        assertThatThrownBy(() -> useCase.ejecutar(acceso)).isInstanceOf(IllegalStateException.class);
    }
}
