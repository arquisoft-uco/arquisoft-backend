package com.arquisoft.fichas.application.estadofichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosFichaPerfilEstudianteUseCaseImplTest {

    @Mock
    private EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosFichaPerfilEstudianteUseCaseImpl useCase;

    @Test
    void debeDelegarEnPuertoConLosUuidDelCriteria_yRetornarSuResultado() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var criteria = new EstadoFichaPerfilEstudianteCriteria(fichaPerfil, estudiante);
        var esperado = List.of(new EstadoFichaPerfilReadModel(
                "EN_CONSTRUCCION", "En Construccion", Instant.now()));
        when(estadoFichaPerfilQueryOutputPort.consultarPorFichaYEstudiante(fichaPerfil, estudiante))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(estadoFichaPerfilQueryOutputPort).consultarPorFichaYEstudiante(fichaPerfil, estudiante);
    }

    @Test
    void debeDevolverListaVacia_cuandoPuertoNoDevuelveNada() {
        // Arrange
        var criteria = new EstadoFichaPerfilEstudianteCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(estadoFichaPerfilQueryOutputPort.consultarPorFichaYEstudiante(any(), any()))
                .thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRegistrarDebugEntradaYCierre_sinInfo() {
        // Arrange
        var criteria = new EstadoFichaPerfilEstudianteCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(estadoFichaPerfilQueryOutputPort.consultarPorFichaYEstudiante(any(), any()))
                .thenReturn(List.of());

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(EstadoFichaPerfilKey.LOG_CONSULTANDO_ESTUDIANTE), eq(criteria.fichaPerfil()));
        verify(logger).debug(eq(EstadoFichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA), eq(0));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
