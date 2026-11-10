package com.arquisoft.mapas_ruta.application.planreal.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
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
class ConsultarPlanesRealesProyectoGradoAsesorUseCaseImplTest {

    @Mock
    private PlanRealQueryOutputPort planRealQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarPlanesRealesProyectoGradoAsesorUseCaseImpl useCase;

    private static PlanRealProyectoReadModel plan() {
        return new PlanRealProyectoReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Tarea", UtilUUID.generarNuevoUUID(), 5, "Plan");
    }

    @Test
    void debeDelegarAlPuertoYRetornarResultado_cuandoSeEjecuta() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var criteria = PlanRealProyectoCriteria.builder().proyectoGrado(proyectoGrado).pagina(0).tamanio(10).build();
        var pagina = PaginatedResult.of(List.of(plan(), plan(), plan()), 0, 10, 3);
        when(planRealQueryOutputPort.consultarPorProyectoGrado(criteria)).thenReturn(pagina);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(pagina);
        var orden = inOrder(logger, planRealQueryOutputPort);
        orden.verify(logger).debug(PlanRealKey.LOG_CONSULTANDO_POR_PROYECTO_GRADO,
                proyectoGrado, 0, 10, criteria.tieneFiltros(), criteria.tieneOrden());
        orden.verify(planRealQueryOutputPort).consultarPorProyectoGrado(criteria);
        orden.verify(logger).debug(PlanRealKey.LOG_CONSULTA_POR_PROYECTO_GRADO_COMPLETADA, 3L, 0, 10);
        verify(planRealQueryOutputPort, times(1)).consultarPorProyectoGrado(criteria);
    }
}
