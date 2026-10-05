package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarCategoriasItemCuantitativoAsesorQueryTest {

    @Test
    void debeNormalizarNombre_cuandoFiltroTraeEspacios() {
        // Arrange
        var nombreFiltro = "  Puntualidad  ";

        // Act
        var query = ConsultarCategoriasItemCuantitativoAsesorQuery.crear(nombreFiltro);

        // Assert
        assertThat(query.nombre()).isEqualTo("Puntualidad");
    }

    @Test
    void debeCrearConNombreNulo_cuandoFiltroEsNuloOVacioOEnBlanco() {
        // Act & Assert
        assertThat(ConsultarCategoriasItemCuantitativoAsesorQuery.crear(null).nombre()).isNull();
        assertThat(ConsultarCategoriasItemCuantitativoAsesorQuery.crear("").nombre()).isNull();
        assertThat(ConsultarCategoriasItemCuantitativoAsesorQuery.crear("   ").nombre()).isNull();
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoNombreExcedeLongitudMaxima() {
        // Arrange
        var nombreFiltro = "n".repeat(101);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarCategoriasItemCuantitativoAsesorQuery.crear(nombreFiltro))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(Tuple.tuple(
                                        EvaluacionesFields.CategoriaItemCuantitativoAsesor.NOMBRE,
                                        EvaluacionesCodes.CategoriaItemCuantitativoAsesor.NOMBRE_FILTRO_DEMASIADO_LARGO)));
    }

    @Test
    void debeCrearQuery_cuandoNombreTieneLongitudMaxima() {
        // Arrange
        var nombreFiltro = "n".repeat(100);

        // Act
        var query = ConsultarCategoriasItemCuantitativoAsesorQuery.crear(nombreFiltro);

        // Assert
        assertThat(query.nombre()).isEqualTo(nombreFiltro);
    }
}
