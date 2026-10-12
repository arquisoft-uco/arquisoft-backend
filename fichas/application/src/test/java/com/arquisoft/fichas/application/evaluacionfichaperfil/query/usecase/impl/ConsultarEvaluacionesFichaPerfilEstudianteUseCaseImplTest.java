package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
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
class ConsultarEvaluacionesFichaPerfilEstudianteUseCaseImplTest {

    @Mock
    private EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesFichaPerfilEstudianteUseCaseImpl useCase;

    @Test
    void debeRetornarEvaluaciones_cuandoPuertoDevuelveResultados() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var criteria = new EvaluacionFichaPerfilEstudianteCriteria(fichaPerfil, estudiante);
        var representante = new RepresentanteComiteReadModel(UtilUUID.generarNuevoUUID(), "María Gómez");
        var esperado = List.of(
                new EvaluacionFichaPerfilEstudianteReadModel(UtilUUID.generarNuevoUUID(), fichaPerfil,
                        Instant.parse("2026-09-01T00:00:00Z"), "DESCARTADA", "Descartada", representante),
                new EvaluacionFichaPerfilEstudianteReadModel(UtilUUID.generarNuevoUUID(), fichaPerfil,
                        Instant.parse("2026-09-10T00:00:00Z"), "EN_EVALUACION", "En Evaluación", representante),
                new EvaluacionFichaPerfilEstudianteReadModel(UtilUUID.generarNuevoUUID(), fichaPerfil,
                        Instant.parse("2026-09-20T00:00:00Z"), "APROBADA", "Aprobada", representante));
        when(evaluacionFichaPerfilQueryOutputPort.consultarPorFichaYEstudiante(fichaPerfil, estudiante))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(evaluacionFichaPerfilQueryOutputPort, times(1)).consultarPorFichaYEstudiante(fichaPerfil, estudiante);
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTANDO_ESTUDIANTE), eq(fichaPerfil));
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA), eq(3));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarListaVacia_cuandoPuertoNoDevuelveResultados() {
        // Arrange
        var criteria = new EvaluacionFichaPerfilEstudianteCriteria(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(evaluacionFichaPerfilQueryOutputPort.consultarPorFichaYEstudiante(any(), any()))
                .thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(EvaluacionFichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA), eq(0));
    }
}
