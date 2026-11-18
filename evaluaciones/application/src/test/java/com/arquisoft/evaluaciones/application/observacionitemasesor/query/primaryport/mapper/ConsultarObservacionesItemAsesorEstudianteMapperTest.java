package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorEstudianteQuery;
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
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarObservacionesItemAsesorEstudianteMapperTest {

    private static ConsultarObservacionesItemAsesorEstudianteQuery query(
            UUID evaluacion, UUID estudiante, int pagina, int tamanio, List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarObservacionesItemAsesorEstudianteQuery.crear(
                evaluacion, estudiante, ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz));
    }

    @Test
    void debeCopiarEvaluacionEstudiantePaginacionYFiltro_alCriteria() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var filtro = NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "rigor");

        // Act
        var criteria = ConsultarObservacionesItemAsesorEstudianteMapper.toCriteria(
                query(evaluacion, estudiante, 2, 5, List.of(), filtro));

        // Assert
        assertThat(criteria.getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
        assertThat(criteria.getEstudiante()).isEqualTo(estudiante);
        assertThat(criteria.getAsesor()).isNull();
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(5);
        assertThat(criteria.getRaiz()).isSameAs(filtro);
    }

    @Test
    void debeAplicarOrdenPorDescripcionAscendente_cuandoElClienteNoPideOrden() {
        // Act
        var criteria = ConsultarObservacionesItemAsesorEstudianteMapper.toCriteria(
                query(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 0, 10, List.of(), null));

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple(
                        ObservacionItemAsesorCriteria.Campo.DESCRIPCION.getClave(), SortDirection.ASC));
    }

    @Test
    void debeConservarElOrdenDelCliente_cuandoLoPide() {
        // Arrange
        var orden = List.of(SortOrder.of("descripcion", SortDirection.DESC));

        // Act
        var criteria = ConsultarObservacionesItemAsesorEstudianteMapper.toCriteria(
                query(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 0, 10, orden, null));

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("descripcion", SortDirection.DESC));
    }

    @Test
    void debeLanzarFiltroException_cuandoElOrdenOElFiltroSonSobreUnCampoNoPermitido() {
        // Arrange
        var ordenInvalido = query(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 0, 10,
                List.of(SortOrder.of("id", SortDirection.ASC)), null);
        var filtroInvalido = query(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 0, 10, List.of(),
                NodoFiltro.predicado("estudiante", FiltroOperador.ES, UtilUUID.generarNuevoUUID().toString()));

        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesItemAsesorEstudianteMapper.toCriteria(ordenInvalido))
                .isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> ConsultarObservacionesItemAsesorEstudianteMapper.toCriteria(filtroInvalido))
                .isInstanceOf(FiltroException.class);
    }
}
