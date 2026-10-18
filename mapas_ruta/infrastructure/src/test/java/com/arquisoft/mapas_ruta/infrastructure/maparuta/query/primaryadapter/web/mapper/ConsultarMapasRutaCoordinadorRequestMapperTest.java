package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarMapasRutaCoordinadorRequestMapperTest {

    @Test
    void debeAplicarValoresPorDefectoYConservarElCoordinador_cuandoElDtoEsNulo() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarMapasRutaCoordinadorRequestMapper.toQuery(null, coordinador);

        // Assert
        assertThat(query.coordinador()).isEqualTo(coordinador);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeLlevarFiltrosYOrdenDelDtoAlCriterio_cuandoElDtoLosTrae() {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("fechaFin:DESC"));
        dto.setFiltros(new PredicadoFiltroDTO("fechaInicio", "MAYOR_IGUAL_QUE", "2026-02-01"));

        // Act
        var query = ConsultarMapasRutaCoordinadorRequestMapper.toQuery(dto, coordinador);

        // Assert
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("fechaFin");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
        assertThat(query.criterio().raiz()).isEqualTo(
                NodoFiltro.predicado("fechaInicio", FiltroOperador.MAYOR_IGUAL_QUE, "2026-02-01"));
    }

    @Test
    void debeLanzarExcepcionDeEntrada_cuandoElCoordinadorEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarMapasRutaCoordinadorRequestMapper.toQuery(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.MapaRuta.COORDINADOR_REQUERIDO));
    }
}
