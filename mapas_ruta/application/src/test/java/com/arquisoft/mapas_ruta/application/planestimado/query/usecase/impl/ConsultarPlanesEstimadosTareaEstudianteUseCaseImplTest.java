package com.arquisoft.mapas_ruta.application.planestimado.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.mapas_ruta.application.planestimado.query.secondaryport.PlanEstimadoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanEstimadoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarPlanesEstimadosTareaEstudianteUseCaseImplTest {

    @Mock
    private PlanEstimadoQueryOutputPort planEstimadoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarPlanesEstimadosTareaEstudianteUseCaseImpl useCase;

    @Test
    void debeConsultarPorTarea_cuandoCriteriaValida() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var criteria = PlanEstimadoTareaCriteria.builder().tarea(tarea).pagina(0).tamanio(10).build();
        var plan = new PlanEstimadoReadModel(UtilUUID.generarNuevoUUID(), tarea, 5, "Revisar");
        var pagina = PaginatedResult.of(List.of(plan), 0, 10, 1);
        when(planEstimadoQueryOutputPort.consultarPorTarea(criteria)).thenReturn(pagina);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(pagina);
        var orden = inOrder(logger, planEstimadoQueryOutputPort);
        orden.verify(logger).debug(PlanEstimadoKey.LOG_CONSULTANDO,
                tarea, 0, 10, criteria.tieneFiltros(), criteria.tieneOrden());
        orden.verify(planEstimadoQueryOutputPort).consultarPorTarea(criteria);
        orden.verify(logger).debug(PlanEstimadoKey.LOG_CONSULTA_COMPLETADA, 1L, 0, 10);
        verify(planEstimadoQueryOutputPort, times(1)).consultarPorTarea(criteria);
    }
}
