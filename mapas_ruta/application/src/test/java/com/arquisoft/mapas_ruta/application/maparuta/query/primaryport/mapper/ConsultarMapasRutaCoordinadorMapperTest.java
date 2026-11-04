package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapasRutaCoordinadorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
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

class ConsultarMapasRutaCoordinadorMapperTest {

    private static NodoFiltro forzado(UUID coordinador) {
        return NodoFiltro.predicado(MapaRutaCriteria.Campo.COORDINADOR.getClave(),
                FiltroOperador.ES, coordinador.toString());
    }

    private static ConsultarMapasRutaCoordinadorQuery query(UUID coordinador, int pagina, int tamanio,
                                                            List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarMapasRutaCoordinadorQuery.crear(coordinador,
                ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz));
    }

    @Test
    void debeCopiarPaginaYTamanio_cuandoSeConvierteElQuery() {
        // Arrange
        var query = query(UtilUUID.generarNuevoUUID(), 2, 25, List.of(), null);

        // Act
        var criteria = ConsultarMapasRutaCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
    }

    @Test
    void debeUsarSoloElPredicadoDelCoordinadorComoRaiz_cuandoElClienteNoFiltra() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var query = query(coordinador, 0, 10, List.of(), null);

        // Act
        var criteria = ConsultarMapasRutaCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(forzado(coordinador));
    }

    @Test
    void debeCombinarConAndElForzadoFueraDelArbolDelCliente_cuandoElClienteFiltra() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("fechaInicio", FiltroOperador.MAYOR_IGUAL_QUE, "2026-02-01");
        var query = query(coordinador, 0, 10, List.of(), raizCliente);

        // Act
        var criteria = ConsultarMapasRutaCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzado(coordinador), raizCliente)));
    }

    @Test
    void debeAnidarElOrDelClienteBajoElAndExterno_cuandoElClienteUsaOr() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var raizOr = NodoFiltro.grupo(FiltroConector.OR, List.of(
                NodoFiltro.predicado("fechaInicio", FiltroOperador.MAYOR_IGUAL_QUE, "2026-02-01"),
                NodoFiltro.predicado("fechaFin", FiltroOperador.MENOR_IGUAL_QUE, "2026-12-01")));
        var query = query(coordinador, 0, 10, List.of(), raizOr);

        // Act
        var criteria = ConsultarMapasRutaCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isInstanceOfSatisfying(NodoFiltro.Grupo.class, raiz -> {
            assertThat(raiz.conector()).isEqualTo(FiltroConector.AND);
            assertThat(raiz.nodos()).containsExactly(forzado(coordinador), raizOr);
        });
    }

    @Test
    void debeAplicarElOrdenPorDefectoConDesempate_cuandoElClienteNoOrdena() {
        // Arrange
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, List.of(), null);

        // Act
        var criteria = ConsultarMapasRutaCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(
                        tuple("fechaInicio", SortDirection.DESC),
                        tuple("fechaFin", SortDirection.ASC));
    }

    @Test
    void debeRespetarElOrdenDelClienteTalCual_cuandoElClienteOrdena() {
        // Arrange
        var ordenCliente = List.of(SortOrder.of("fechaFin", SortDirection.DESC));
        var query = query(UtilUUID.generarNuevoUUID(), 0, 10, ordenCliente, null);

        // Act
        var criteria = ConsultarMapasRutaCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("fechaFin", SortDirection.DESC));
    }

    @Test
    void debeLanzarFiltroException_cuandoElClienteFiltraPorCamposFueraDeLaWhitelist() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var porTitulo = query(coordinador, 0, 10, List.of(),
                NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "web"));
        var porProyecto = query(coordinador, 0, 10, List.of(),
                NodoFiltro.predicado("proyectoGrado", FiltroOperador.ES, UtilUUID.generarNuevoUUID().toString()));

        // Act & Assert
        assertThatThrownBy(() -> ConsultarMapasRutaCoordinadorMapper.toCriteria(porTitulo))
                .isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> ConsultarMapasRutaCoordinadorMapper.toCriteria(porProyecto))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElClienteOrdenaPorCamposNoOrdenables() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        for (var campo : List.of("tituloProyecto", "proyectoGrado", "id", "coordinador")) {
            var query = query(coordinador, 0, 10, List.of(SortOrder.of(campo, SortDirection.ASC)), null);
            assertThatThrownBy(() -> ConsultarMapasRutaCoordinadorMapper.toCriteria(query))
                    .as(campo)
                    .isInstanceOf(FiltroException.class);
        }
    }
}
