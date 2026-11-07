package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarItemCuantitativoAsesorCommandTest {

    @Test
    void debeCrearCommandRecortado_cuandoDatosValidos() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();

        // Act
        var command = RegistrarItemCuantitativoAsesorCommand.crear(
                "  Avance  ", "  Porcentaje de avance  ", categoria.toString(), 500);

        // Assert
        assertThat(command.nombre()).isEqualTo("Avance");
        assertThat(command.descripcion()).isEqualTo("Porcentaje de avance");
        assertThat(command.categoria()).isEqualTo(categoria);
        assertThat(command.valor()).isEqualTo(500);
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoDatosInvalidos() {
        // Arrange
        var nombre = " ";
        var descripcion = "d".repeat(301);
        var categoria = "no-es-uuid";
        var valor = 501;

        // Act & Assert
        assertThatThrownBy(() -> RegistrarItemCuantitativoAsesorCommand.crear(nombre, descripcion, categoria, valor))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.NOMBRE,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.NOMBRE_REQUERIDO),
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_DEMASIADO_LARGA),
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.CATEGORIA,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.CATEGORIA_REQUERIDA),
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.VALOR,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.VALOR_FUERA_DE_RANGO)));
    }

    @Test
    void debeExigirValorYCategoria_cuandoLlegaNulo() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarItemCuantitativoAsesorCommand.crear("Avance", "Desc", null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ItemCuantitativoAsesor.CATEGORIA_REQUERIDA,
                                        EvaluacionesCodes.ItemCuantitativoAsesor.VALOR_REQUERIDO));
    }
}
