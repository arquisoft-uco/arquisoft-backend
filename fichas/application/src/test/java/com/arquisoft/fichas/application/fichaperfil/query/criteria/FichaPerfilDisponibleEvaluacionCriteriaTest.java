package com.arquisoft.fichas.application.fichaperfil.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilDisponibleEvaluacionCriteriaTest {

    @ParameterizedTest
    @ValueSource(strings = {"tituloProyecto", "asesorNombre", "asesorEmail"})
    void debeAceptarFiltrarYOrdenar_cuandoElCampoEstaPermitido(String clave) {
        // Arrange
        var raiz = NodoFiltro.predicado(clave, FiltroOperador.CONTIENE, "web");
        var orden = List.of(SortOrder.of(clave, SortDirection.ASC));

        // Act
        var criteria = FichaPerfilDisponibleEvaluacionCriteria.builder()
                .raiz(raiz).ordenamiento(orden).build();

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"estadoFicha", "asesorId", "campoInexistente"})
    void debeLanzarFiltroException_cuandoFiltraOOrdenaPorCampoNoPermitido(String clave) {
        // Arrange
        var raiz = NodoFiltro.predicado(clave, FiltroOperador.ES, "X");
        var orden = List.of(SortOrder.of(clave, SortDirection.ASC));
        var builder = FichaPerfilDisponibleEvaluacionCriteria.builder();

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> builder.ordenamiento(orden)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeConservarElRepresentante_cuandoSeConstruyeElCriteria() {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();

        // Act
        var criteria = FichaPerfilDisponibleEvaluacionCriteria.builder()
                .representanteComite(representante).build();

        // Assert
        assertThat(criteria.getRepresentanteComite()).isEqualTo(representante);
    }
}
