package com.arquisoft.mapas_ruta.application.tarea.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaMapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.secondaryport.TareaQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTareasMapaRutaEstudianteUseCaseImplTest {

    @Mock
    private TareaQueryOutputPort tareaQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarTareasMapaRutaEstudianteUseCaseImpl useCase;

    private static TareaReadModel tarea(UUID mapaRuta) {
        return new TareaReadModel(UtilUUID.generarNuevoUUID(), "Tarea", mapaRuta, LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 1), "NUEVA", "Nueva", UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co");
    }

    @Test
    void debeConsultarTareas_delegandoEnElPuerto() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var criteria = TareaMapaRutaCriteria.builder().mapaRuta(mapaRuta).pagina(0).tamanio(10).build();
        var pagina = PaginatedResult.of(List.of(tarea(mapaRuta), tarea(mapaRuta), tarea(mapaRuta)), 0, 10, 3);
        when(tareaQueryOutputPort.consultarPorMapaRuta(criteria)).thenReturn(pagina);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(pagina);
        var orden = inOrder(logger, tareaQueryOutputPort);
        orden.verify(logger).debug(TareaKey.LOG_CONSULTANDO_POR_MAPA_RUTA,
                mapaRuta, 0, 10, criteria.tieneFiltros(), criteria.tieneOrden());
        orden.verify(tareaQueryOutputPort).consultarPorMapaRuta(criteria);
        orden.verify(logger).debug(TareaKey.LOG_CONSULTA_POR_MAPA_RUTA_COMPLETADA, 3L, 0, 10);
        verify(tareaQueryOutputPort, times(1)).consultarPorMapaRuta(criteria);
    }
}
