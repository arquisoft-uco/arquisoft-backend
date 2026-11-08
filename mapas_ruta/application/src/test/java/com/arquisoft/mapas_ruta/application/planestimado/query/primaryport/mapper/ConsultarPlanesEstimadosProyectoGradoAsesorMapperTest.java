package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosProyectoGradoAsesorQuery;
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

class ConsultarPlanesEstimadosProyectoGradoAsesorMapperTest {

    private static ConsultarPlanesEstimadosProyectoGradoAsesorQuery query(UUID proyectoGrado, int pagina, int tamanio,
                                                                          List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarPlanesEstimadosProyectoGradoAsesorQuery.crear(proyectoGrado,
                ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz));
    }

    @Test
    void debeConstruirCriteria_conProyectoGradoPaginaYFiltros() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var raiz = NodoFiltro.predicado("descripcionTarea", FiltroOperador.CONTIENE, "cap");
        var query = query(proyectoGrado, 2, 25, List.of(), raiz);

        // Act
        var criteria = ConsultarPlanesEstimadosProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeAplicarOrdenPorDefecto_cuandoNoHayOrden() {
        // Arrange
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, List.of(), null);

        // Act
        var criteria = ConsultarPlanesEstimadosProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("tiempoEstimadoDias", SortDirection.ASC));
    }

    @Test
    void debeRespetarOrden_cuandoSeSolicita() {
        // Arrange
        var orden = List.of(SortOrder.of("descripcionTarea", SortDirection.DESC));
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, orden, null);

        // Act
        var criteria = ConsultarPlanesEstimadosProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("descripcionTarea", SortDirection.DESC));
    }

    @Test
    void debeLanzarFiltroException_cuandoFiltraOOrdenaPorCampoNoPermitido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var filtroInvalido = query(proyectoGrado, 0, 10, List.of(),
                NodoFiltro.predicado("mapaRuta", FiltroOperador.ES, UtilUUID.generarNuevoUUID().toString()));
        var ordenInvalido = query(proyectoGrado, 0, 10, List.of(SortOrder.of("tarea", SortDirection.ASC)), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarPlanesEstimadosProyectoGradoAsesorMapper.toCriteria(filtroInvalido))
                .isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> ConsultarPlanesEstimadosProyectoGradoAsesorMapper.toCriteria(ordenInvalido))
                .isInstanceOf(FiltroException.class);
    }
}
