package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesFichaPerfilCoordinadorUseCaseImplTest {

    @Mock
    private EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesFichaPerfilCoordinadorUseCaseImpl useCase;

    @Test
    void debeRetornarEvaluaciones_cuandoElPuertoDevuelveResultados() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionFichaPerfilCoordinadorCriteria(fichaPerfil);
        var representante = new RepresentanteComiteReadModel(UtilUUID.generarNuevoUUID(), "María Gómez");
        var esperado = List.of(
                new EvaluacionFichaPerfilCoordinadorReadModel(UtilUUID.generarNuevoUUID(), fichaPerfil,
                        Instant.parse("2026-09-01T00:00:00Z"), "DESCARTADA", "Descartada", representante),
                new EvaluacionFichaPerfilCoordinadorReadModel(UtilUUID.generarNuevoUUID(), fichaPerfil,
                        Instant.parse("2026-09-10T00:00:00Z"), "EN_EVALUACION", "En Evaluación", representante),
                new EvaluacionFichaPerfilCoordinadorReadModel(UtilUUID.generarNuevoUUID(), fichaPerfil,
                        Instant.parse("2026-09-20T00:00:00Z"), "APROBADA", "Aprobada", representante));
        when(evaluacionFichaPerfilQueryOutputPort.consultarPorFicha(fichaPerfil)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(evaluacionFichaPerfilQueryOutputPort, times(1)).consultarPorFicha(fichaPerfil);
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTANDO_COORDINADOR), eq(fichaPerfil));
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTA_COORDINADOR_COMPLETADA), eq(3));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEvaluaciones() {
        // Arrange
        var criteria = new EvaluacionFichaPerfilCoordinadorCriteria(UtilUUID.generarNuevoUUID());
        when(evaluacionFichaPerfilQueryOutputPort.consultarPorFicha(any())).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTA_COORDINADOR_COMPLETADA), eq(0));
    }
}
