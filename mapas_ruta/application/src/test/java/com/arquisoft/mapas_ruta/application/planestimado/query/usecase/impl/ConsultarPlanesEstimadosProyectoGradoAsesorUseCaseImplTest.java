package com.arquisoft.mapas_ruta.application.planestimado.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
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
class ConsultarPlanesEstimadosProyectoGradoAsesorUseCaseImplTest {

    @Mock
    private PlanEstimadoQueryOutputPort planEstimadoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarPlanesEstimadosProyectoGradoAsesorUseCaseImpl useCase;

    private static PlanEstimadoProyectoReadModel plan() {
        return new PlanEstimadoProyectoReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Tarea", UtilUUID.generarNuevoUUID(), 5, "Plan");
    }

    @Test
    void debeConsultarPlanes_delegandoEnElPuerto() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var criteria = PlanEstimadoProyectoCriteria.builder().proyectoGrado(proyectoGrado).pagina(0).tamanio(10).build();
        var pagina = PaginatedResult.of(List.of(plan(), plan(), plan()), 0, 10, 3);
        when(planEstimadoQueryOutputPort.consultarPorProyectoGrado(criteria)).thenReturn(pagina);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(pagina);
        var orden = inOrder(logger, planEstimadoQueryOutputPort);
        orden.verify(logger).debug(PlanEstimadoKey.LOG_CONSULTANDO_POR_PROYECTO_GRADO,
                proyectoGrado, 0, 10, criteria.tieneFiltros(), criteria.tieneOrden());
        orden.verify(planEstimadoQueryOutputPort).consultarPorProyectoGrado(criteria);
        orden.verify(logger).debug(PlanEstimadoKey.LOG_CONSULTA_POR_PROYECTO_GRADO_COMPLETADA, 3L, 0, 10);
        verify(planEstimadoQueryOutputPort, times(1)).consultarPorProyectoGrado(criteria);
    }
}
