package com.arquisoft.mapas_ruta.application.planreal.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.mapas_ruta.application.planreal.query.secondaryport.PlanRealQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
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
class ConsultarPlanesRealesTareaEstudianteUseCaseImplTest {

    @Mock
    private PlanRealQueryOutputPort planRealQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarPlanesRealesTareaEstudianteUseCaseImpl useCase;

    @Test
    void debeConsultarPorTarea_cuandoCriteriaValida() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var criteria = PlanRealTareaCriteria.builder().tarea(tarea).pagina(0).tamanio(10).build();
        var plan = new PlanRealReadModel(UtilUUID.generarNuevoUUID(), tarea, 5, "Revisar");
        var pagina = PaginatedResult.of(List.of(plan), 0, 10, 1);
        when(planRealQueryOutputPort.consultarPorTarea(criteria)).thenReturn(pagina);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(pagina);
        var orden = inOrder(logger, planRealQueryOutputPort);
        orden.verify(logger).debug(PlanRealKey.LOG_CONSULTANDO,
                tarea, 0, 10, criteria.tieneFiltros(), criteria.tieneOrden());
        orden.verify(planRealQueryOutputPort).consultarPorTarea(criteria);
        orden.verify(logger).debug(PlanRealKey.LOG_CONSULTA_COMPLETADA, 1L, 0, 10);
        verify(planRealQueryOutputPort, times(1)).consultarPorTarea(criteria);
    }
}
