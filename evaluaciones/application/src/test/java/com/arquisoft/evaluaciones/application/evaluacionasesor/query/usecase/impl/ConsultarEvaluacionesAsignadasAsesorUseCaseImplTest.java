package com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsignadaAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEvaluacionesAsignadasAsesorUseCaseImplTest {

    @Mock
    private EvaluacionAsignadaAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarEvaluacionesAsignadasAsesorUseCaseImpl useCase;

    private static EvaluacionAsignadaAsesorReadModel asignacion() {
        return new EvaluacionAsignadaAsesorReadModel(
                UtilUUID.generarNuevoUUID(),
                new EvaluacionAsignadaAsesorReadModel.Evaluacion(UtilUUID.generarNuevoUUID(),
                        new EvaluacionAsignadaAsesorReadModel.Estado("PENDIENTE", "Pendiente")),
                new EvaluacionAsignadaAsesorReadModel.Entregable(UtilUUID.generarNuevoUUID(), "Proyecto", 1),
                false, true, null);
    }

    @Test
    void debeRetornarPaginaDeEvaluaciones_cuandoElAsesorTieneAsociadas() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criteria = EvaluacionesAsignadasAsesorCriteria.builder().asesor(asesor).pagina(0).tamanio(10).build();
        var esperado = PaginatedResult.of(List.of(asignacion(), asignacion(), asignacion()), 0, 10, 3);
        when(queryOutputPort.consultarTodas(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(queryOutputPort, times(1)).consultarTodas(criteria);
        verify(logger).debug(EvaluacionAsesorKey.LOG_CONSULTA_ASIGNADAS_COMPLETADA, 3L, 0, 10);
    }
}
