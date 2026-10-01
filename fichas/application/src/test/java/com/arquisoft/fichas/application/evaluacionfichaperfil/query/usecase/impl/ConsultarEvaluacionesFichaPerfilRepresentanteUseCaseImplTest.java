package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
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
class ConsultarEvaluacionesFichaPerfilRepresentanteUseCaseImplTest {

    @Mock
    private EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesFichaPerfilRepresentanteUseCaseImpl useCase;

    @Test
    void debeDelegarEnPuertoConLosIdsDelCriteria_yRetornarSuResultado() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var representanteComite = UUID.randomUUID();
        var criteria = new EvaluacionFichaPerfilRepresentanteCriteria(fichaPerfil, representanteComite);
        var esperado = List.of(new EvaluacionFichaPerfilReadModel(
                UUID.randomUUID(), fichaPerfil, Instant.now(), "EN_EVALUACION", "En Evaluación"));
        when(evaluacionFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(fichaPerfil, representanteComite))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(evaluacionFichaPerfilQueryOutputPort).consultarPorFichaYRepresentante(fichaPerfil, representanteComite);
    }

    @Test
    void debeDevolverListaVacia_cuandoElPuertoNoRetornaNada() {
        // Arrange
        var criteria = new EvaluacionFichaPerfilRepresentanteCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(evaluacionFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(any(), any()))
                .thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRegistrarDebugEntradaYCierre_sinInfo() {
        // Arrange
        var criteria = new EvaluacionFichaPerfilRepresentanteCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(evaluacionFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(any(), any()))
                .thenReturn(List.of());

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTANDO_REPRESENTANTE), eq(criteria.fichaPerfil()));
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA), eq(0));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
