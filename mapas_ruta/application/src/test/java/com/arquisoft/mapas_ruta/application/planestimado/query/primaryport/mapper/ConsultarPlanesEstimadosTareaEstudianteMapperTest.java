package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosTareaEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class ConsultarPlanesEstimadosTareaEstudianteMapperTest {

    private static ConsultarPlanesEstimadosTareaEstudianteQuery query(UUID tarea, int pagina, int tamanio,
                                                                      List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarPlanesEstimadosTareaEstudianteQuery.crear(tarea,
                ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz));
    }

    @Test
    void debeConstruirCriteria_conTareaPaginaYFiltros() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var raiz = NodoFiltro.predicado("descripcionEstimado", FiltroOperador.CONTIENE, "rev");
        var query = query(tarea, 2, 25, List.of(), raiz);

        // Act
        var criteria = ConsultarPlanesEstimadosTareaEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getTarea()).isEqualTo(tarea);
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeAplicarOrdenPorDefecto_cuandoNoHayOrden() {
        // Arrange
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, List.of(), null);

        // Act
        var criteria = ConsultarPlanesEstimadosTareaEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("tiempoEstimadoDias", SortDirection.ASC));
    }

    @Test
    void debeRespetarOrden_cuandoSeSolicita() {
        // Arrange
        var orden = List.of(SortOrder.of("descripcionEstimado", SortDirection.DESC));
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, orden, null);

        // Act
        var criteria = ConsultarPlanesEstimadosTareaEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("descripcionEstimado", SortDirection.DESC));
    }

    @Test
    void debeLanzarFiltroException_cuandoFiltraOOrdenaPorCampoNoPermitido() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var filtroInvalido = query(tarea, 0, 10, List.of(),
                NodoFiltro.predicado("tarea", FiltroOperador.ES, UtilUUID.generarNuevoUUID().toString()));
        var ordenInvalido = query(tarea, 0, 10, List.of(SortOrder.of("tarea", SortDirection.ASC)), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarPlanesEstimadosTareaEstudianteMapper.toCriteria(filtroInvalido))
                .isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> ConsultarPlanesEstimadosTareaEstudianteMapper.toCriteria(ordenInvalido))
                .isInstanceOf(FiltroException.class);
    }
}
