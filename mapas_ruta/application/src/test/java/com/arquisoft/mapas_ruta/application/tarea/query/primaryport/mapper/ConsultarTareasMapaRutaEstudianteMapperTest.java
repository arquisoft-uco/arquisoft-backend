package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaEstudianteQuery;
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

class ConsultarTareasMapaRutaEstudianteMapperTest {

    private static ConsultarTareasMapaRutaEstudianteQuery query(UUID mapaRuta, int pagina, int tamanio,
                                                                List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarTareasMapaRutaEstudianteQuery.crear(mapaRuta,
                ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz));
    }

    @Test
    void debeConstruirCriteria_conMapaRutaPaginaYFiltros() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var raiz = NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "cap");
        var query = query(mapaRuta, 2, 25, List.of(), raiz);

        // Act
        var criteria = ConsultarTareasMapaRutaEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getMapaRuta()).isEqualTo(mapaRuta);
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeAplicarOrdenPorDefecto_cuandoNoHayOrden() {
        // Arrange
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, List.of(), null);

        // Act
        var criteria = ConsultarTareasMapaRutaEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("descripcion", SortDirection.ASC));
    }

    @Test
    void debeRespetarOrden_cuandoSeSolicita() {
        // Arrange
        var orden = List.of(SortOrder.of("estadoItem", SortDirection.DESC));
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, orden, null);

        // Act
        var criteria = ConsultarTareasMapaRutaEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("estadoItem", SortDirection.DESC));
    }

    @Test
    void debeLanzarFiltroException_cuandoFiltraOOrdenaPorCampoNoPermitido() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var filtroInvalido = query(mapaRuta, 0, 10, List.of(),
                NodoFiltro.predicado("mapaRuta", FiltroOperador.ES, UtilUUID.generarNuevoUUID().toString()));
        var ordenInvalido = query(mapaRuta, 0, 10, List.of(SortOrder.of("asesor", SortDirection.ASC)), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarTareasMapaRutaEstudianteMapper.toCriteria(filtroInvalido))
                .isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> ConsultarTareasMapaRutaEstudianteMapper.toCriteria(ordenInvalido))
                .isInstanceOf(FiltroException.class);
    }
}
