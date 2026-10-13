package com.arquisoft.fichas.application.estadofichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadosFichaPerfilRepresentanteUseCaseImplTest {

    @Mock
    private EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEstadosFichaPerfilRepresentanteUseCaseImpl useCase;

    @Test
    void debeRetornarEstados_cuandoElPuertoDevuelveResultados() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var criteria = new EstadoFichaPerfilRepresentanteCriteria(fichaPerfil, representanteComite);
        var t0 = Instant.now().minus(2, ChronoUnit.HOURS);
        var esperado = List.of(
                new EstadoFichaPerfilReadModel("EN_CONSTRUCCION", "En Construccion", t0),
                new EstadoFichaPerfilReadModel("DISPONIBLE_PARA_EVALUACION", "Disponible Para Evaluacion",
                        t0.plus(1, ChronoUnit.HOURS)),
                new EstadoFichaPerfilReadModel("APROBADA", "Aprobada", t0.plus(2, ChronoUnit.HOURS)));
        when(estadoFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(fichaPerfil, representanteComite))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(estadoFichaPerfilQueryOutputPort, times(1))
                .consultarPorFichaYRepresentante(fichaPerfil, representanteComite);
        verify(logger).debug(eq(EstadoFichaPerfilKey.LOG_CONSULTANDO_REPRESENTANTE), eq(fichaPerfil));
        verify(logger).debug(eq(EstadoFichaPerfilKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA), eq(3));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarListaVacia_cuandoElRepresentanteNoEvaluaLaFicha() {
        // Arrange
        var criteria = new EstadoFichaPerfilRepresentanteCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(estadoFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(any(), any()))
                .thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(EstadoFichaPerfilKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA), eq(0));
    }
}
