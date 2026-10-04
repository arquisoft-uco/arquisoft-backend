package com.arquisoft.usuarios.application.usuario.query.primaryport.mapper;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarUsuariosAdministradorMapperTest {

    @Test
    void debeConstruirCriteria_cuandoQueryTraePaginacionOrdenYFiltros() {
        // Arrange
        var raiz = NodoFiltro.predicado("nombre", FiltroOperador.CONTIENE, "ana");
        var query = ConsultaCriteriaQuery.crear(
                2, 25, List.of(SortOrder.of("email", SortDirection.DESC)), raiz);

        // Act
        var criteria = ConsultarUsuariosAdministradorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("email");
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeAceptarGrupoDeRolesYVigencia_cuandoFiltraPorLosFlagsBooleanos() {
        // Arrange
        var raiz = NodoFiltro.grupo(FiltroConector.AND, List.of(
                NodoFiltro.grupo(FiltroConector.OR, List.of(
                        NodoFiltro.predicado("esEstudiante", FiltroOperador.ES, "true"),
                        NodoFiltro.predicado("esAsesor", FiltroOperador.ES, "true"),
                        NodoFiltro.predicado("esAsesorFicha", FiltroOperador.ES, "true"),
                        NodoFiltro.predicado("esCoordinador", FiltroOperador.ES, "true"),
                        NodoFiltro.predicado("esRepresentanteComite", FiltroOperador.ES, "true"),
                        NodoFiltro.predicado("esAdministrador", FiltroOperador.ES, "true"),
                        NodoFiltro.predicado("esBibliotecario", FiltroOperador.ES, "true"))),
                NodoFiltro.predicado("vigente", FiltroOperador.ES, "true"),
                NodoFiltro.predicado("estado", FiltroOperador.ES, "ACTIVO")));
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);

        // Act
        var criteria = ConsultarUsuariosAdministradorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.tieneFiltros()).isTrue();
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeLanzarFiltroException_cuandoFiltraPorCampoNoPermitido() {
        // Arrange
        var raiz = NodoFiltro.predicado("id", FiltroOperador.ES, "x");
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);

        // Act
        var lanzamiento = assertThatThrownBy(() -> ConsultarUsuariosAdministradorMapper.toCriteria(query));

        // Assert
        lanzamiento.isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorUnFlagDeRol() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("esEstudiante", SortDirection.ASC)), null);

        // Act
        var lanzamiento = assertThatThrownBy(() -> ConsultarUsuariosAdministradorMapper.toCriteria(query));

        // Assert
        lanzamiento.isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorEsRepresentanteComite() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("esRepresentanteComite", SortDirection.DESC)), null);

        // Act
        var lanzamiento = assertThatThrownBy(() -> ConsultarUsuariosAdministradorMapper.toCriteria(query));

        // Assert
        lanzamiento.isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorEsAdministrador() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("esAdministrador", SortDirection.ASC)), null);

        // Act
        var lanzamiento = assertThatThrownBy(() -> ConsultarUsuariosAdministradorMapper.toCriteria(query));

        // Assert
        lanzamiento.isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorEsBibliotecario() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("esBibliotecario", SortDirection.DESC)), null);

        // Act
        var lanzamiento = assertThatThrownBy(() -> ConsultarUsuariosAdministradorMapper.toCriteria(query));

        // Assert
        lanzamiento.isInstanceOf(FiltroException.class);
    }
}
