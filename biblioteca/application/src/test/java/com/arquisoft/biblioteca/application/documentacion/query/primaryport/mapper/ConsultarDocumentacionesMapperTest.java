package com.arquisoft.biblioteca.application.documentacion.query.primaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarDocumentacionesQuery;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarDocumentacionesMapperTest {

    private static final NodoFiltro PUBLICADO =
            NodoFiltro.predicado("estadoDocumentacion", FiltroOperador.ES, "PUBLICADO");

    private static ConsultarDocumentacionesQuery query(List<RolConsultaDocumentacion> roles, NodoFiltro raiz) {
        return ConsultarDocumentacionesQuery.crear(roles, ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz));
    }

    @Test
    void debeConservarLaRaizDelCliente_cuandoEsBibliotecario() {
        // Arrange
        var raiz = NodoFiltro.predicado("estadoDocumentacion", FiltroOperador.ES, "PENDIENTE");

        // Act
        var criteria = ConsultarDocumentacionesMapper.toCriteria(
                query(List.of(RolConsultaDocumentacion.BIBLIOTECARIO), raiz));

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeConservarRaizNula_cuandoEsBibliotecarioSinFiltros() {
        // Act
        var criteria = ConsultarDocumentacionesMapper.toCriteria(
                query(List.of(RolConsultaDocumentacion.BIBLIOTECARIO), null));

        // Assert
        assertThat(criteria.getRaiz()).isNull();
    }

    @Test
    void debeForzarPublicado_cuandoEsAsesorCoordinadorOEstudiante() {
        // Arrange
        var roles = List.of(RolConsultaDocumentacion.ASESOR, RolConsultaDocumentacion.COORDINADOR,
                RolConsultaDocumentacion.ESTUDIANTE);

        // Act & Assert
        for (var rol : roles) {
            var criteria = ConsultarDocumentacionesMapper.toCriteria(query(List.of(rol), null));
            assertThat(criteria.getRaiz()).isEqualTo(PUBLICADO);
        }
    }

    @Test
    void debeForzarPublicado_cuandoLosRolesVienenVacios() {
        // Act
        var criteria = ConsultarDocumentacionesMapper.toCriteria(query(List.of(), null));

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(PUBLICADO);
    }

    @Test
    void debeCombinarConAnd_cuandoElClienteFiltraPorEstado() {
        // Arrange
        var delCliente = NodoFiltro.predicado("estadoDocumentacion", FiltroOperador.ES, "PENDIENTE");

        // Act
        var criteria = ConsultarDocumentacionesMapper.toCriteria(
                query(List.of(RolConsultaDocumentacion.ESTUDIANTE), delCliente));

        // Assert
        assertThat(criteria.getRaiz()).isInstanceOfSatisfying(NodoFiltro.Grupo.class, grupo -> {
            assertThat(grupo.conector()).isEqualTo(FiltroConector.AND);
            assertThat(grupo.nodos()).containsExactly(PUBLICADO, delCliente);
        });
    }

    @Test
    void debeVerTodo_cuandoTieneBibliotecarioYOtroRol() {
        // Act
        var criteria = ConsultarDocumentacionesMapper.toCriteria(query(
                List.of(RolConsultaDocumentacion.ASESOR, RolConsultaDocumentacion.BIBLIOTECARIO), null));

        // Assert
        assertThat(criteria.getRaiz()).isNull();
    }

    @Test
    void debeOrdenarPorFechaDescendente_cuandoNoHayOrden() {
        // Act
        var criteria = ConsultarDocumentacionesMapper.toCriteria(query(List.of(), null));

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("fechaPublicacion");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeRespetarOrdenYPaginacionDelCliente_cuandoLosEnvia() {
        // Arrange
        var orden = List.of(SortOrder.of("fechaPublicacion", SortDirection.ASC));
        var criterio = ConsultaCriteriaQuery.crear(3, 25, orden, null);
        var query = ConsultarDocumentacionesQuery.crear(List.of(RolConsultaDocumentacion.BIBLIOTECARIO), criterio);

        // Act
        var criteria = ConsultarDocumentacionesMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(3);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }
}
