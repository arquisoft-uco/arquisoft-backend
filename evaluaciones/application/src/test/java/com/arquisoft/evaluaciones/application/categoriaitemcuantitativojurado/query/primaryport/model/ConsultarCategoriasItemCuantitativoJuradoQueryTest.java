package com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarCategoriasItemCuantitativoJuradoQueryTest {

    @Test
    void debeNormalizarNombre_cuandoFiltroTraeEspacios() {
        // Arrange
        var nombreFiltro = "  Puntualidad  ";

        // Act
        var query = ConsultarCategoriasItemCuantitativoJuradoQuery.crear(nombreFiltro);

        // Assert
        assertThat(query.nombre()).isEqualTo("Puntualidad");
    }

    @Test
    void debeCrearConNombreNulo_cuandoFiltroEsNuloOVacioOEnBlanco() {
        // Act & Assert
        assertThat(ConsultarCategoriasItemCuantitativoJuradoQuery.crear(null).nombre()).isNull();
        assertThat(ConsultarCategoriasItemCuantitativoJuradoQuery.crear("").nombre()).isNull();
        assertThat(ConsultarCategoriasItemCuantitativoJuradoQuery.crear("   ").nombre()).isNull();
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoNombreExcedeLongitudMaxima() {
        // Arrange
        var nombreFiltro = "n".repeat(101);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarCategoriasItemCuantitativoJuradoQuery.crear(nombreFiltro))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(Tuple.tuple(
                                        EvaluacionesFields.CategoriaItemCuantitativoJurado.NOMBRE,
                                        EvaluacionesCodes.CategoriaItemCuantitativoJurado.NOMBRE_FILTRO_DEMASIADO_LARGO)));
    }
}
