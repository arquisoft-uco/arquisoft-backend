package com.arquisoft.mapas_ruta.application.maparuta.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.mapas_ruta.application.maparuta.query.secondaryport.MapaRutaQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.MapaRutaKey;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarMapasRutaCoordinadorUseCaseImplTest {

    @Mock
    private MapaRutaQueryOutputPort mapaRutaQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarMapasRutaCoordinadorUseCaseImpl useCase;

    private static MapaRutaCriteria criteria() {
        return MapaRutaCriteria.builder()
                .pagina(1)
                .tamanio(20)
                .ordenamiento(List.of(SortOrder.of("fechaInicio", SortDirection.DESC)))
                .raiz(NodoFiltro.predicado("coordinador", FiltroOperador.ES,
                        UtilUUID.generarNuevoUUID().toString()))
                .build();
    }

    private static MapaRutaReadModel readModel(String titulo) {
        return new MapaRutaReadModel(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), titulo,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 6, 1));
    }

    @Test
    void debeDelegarAlPuertoYRetornarLaPagina_cuandoHayResultados() {
        // Arrange
        var criteria = criteria();
        var pagina = PaginatedResult.of(
                List.of(readModel("Uno"), readModel("Dos"), readModel("Tres")), 1, 20, 23L);
        when(mapaRutaQueryOutputPort.consultarTodos(criteria)).thenReturn(pagina);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(pagina);
        var orden = inOrder(logger, mapaRutaQueryOutputPort);
        orden.verify(logger).debug(MapaRutaKey.LOG_CONSULTANDO, 1, 20, true, true);
        orden.verify(mapaRutaQueryOutputPort).consultarTodos(criteria);
        orden.verify(logger).debug(MapaRutaKey.LOG_CONSULTA_COMPLETADA, 23L, 1, 20);
        verify(mapaRutaQueryOutputPort, times(1)).consultarTodos(criteria);
    }

    @Test
    void debeRetornarLaPaginaVacia_cuandoElPuertoNoDevuelveResultados() {
        // Arrange
        var criteria = criteria();
        PaginatedResult<MapaRutaReadModel> vacia = PaginatedResult.of(List.of(), 1, 20, 0L);
        when(mapaRutaQueryOutputPort.consultarTodos(criteria)).thenReturn(vacia);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
        verify(logger).debug(MapaRutaKey.LOG_CONSULTA_COMPLETADA, 0L, 1, 20);
    }
}
