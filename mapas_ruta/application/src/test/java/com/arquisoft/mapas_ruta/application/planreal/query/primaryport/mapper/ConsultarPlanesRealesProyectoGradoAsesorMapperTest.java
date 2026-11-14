package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesProyectoGradoAsesorQuery;
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

class ConsultarPlanesRealesProyectoGradoAsesorMapperTest {

    private static ConsultarPlanesRealesProyectoGradoAsesorQuery query(UUID proyectoGrado, int pagina, int tamanio,
                                                                       List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarPlanesRealesProyectoGradoAsesorQuery.crear(proyectoGrado,
                ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz));
    }

    @Test
    void debeMapearCriteria_cuandoQueryValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var raiz = NodoFiltro.predicado("descripcionTarea", FiltroOperador.CONTIENE, "cap");
        var query = query(proyectoGrado, 2, 25, List.of(), raiz);

        // Act
        var criteria = ConsultarPlanesRealesProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeAplicarOrdenPorDefecto_cuandoNoHayOrdenamiento() {
        // Arrange
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, List.of(), null);

        // Act
        var criteria = ConsultarPlanesRealesProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("tiempoRealDias", SortDirection.ASC));
    }

    @Test
    void debeConservarOrdenamiento_cuandoSeSolicita() {
        // Arrange
        var orden = List.of(SortOrder.of("descripcionTarea", SortDirection.DESC));
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, orden, null);

        // Act
        var criteria = ConsultarPlanesRealesProyectoGradoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("descripcionTarea", SortDirection.DESC));
    }

    @Test
    void debeLanzarFiltroInvalido_cuandoCampoFiltroNoPermitido() {
        // Arrange
        var filtroInvalido = query(UtilUUID.generarNuevoUUID(), 0, 10, List.of(),
                NodoFiltro.predicado("mapaRuta", FiltroOperador.ES, UtilUUID.generarNuevoUUID().toString()));

        // Act & Assert
        assertThatThrownBy(() -> ConsultarPlanesRealesProyectoGradoAsesorMapper.toCriteria(filtroInvalido))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroInvalido_cuandoCampoOrdenNoPermitido() {
        // Arrange
        var ordenInvalido = query(UtilUUID.generarNuevoUUID(), 0, 10,
                List.of(SortOrder.of("tarea", SortDirection.ASC)), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarPlanesRealesProyectoGradoAsesorMapper.toCriteria(ordenInvalido))
                .isInstanceOf(FiltroException.class);
    }
}
