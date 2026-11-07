package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ItemCuantitativoAsesorDomainTest {

    @Test
    void debeCrearYRecortarTextos_cuandoDatosValidos() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();

        // Act
        var item = ItemCuantitativoAsesorDomain.crear("  Avance  ", "  Porcentaje de avance  ", categoria, 100);

        // Assert
        assertThat(item.getId()).isNotNull();
        assertThat(item.getNombre()).isEqualTo("Avance");
        assertThat(item.getDescripcion()).isEqualTo("Porcentaje de avance");
        assertThat(item.getCategoria()).isEqualTo(categoria);
        assertThat(item.getValor()).isEqualTo(100);
    }

    @Test
    void debeAcumularFieldErrors_cuandoTodosLosCamposSonInvalidos() {
        // Arrange
        var nombre = "n".repeat(101);
        var descripcion = " ";

        // Act & Assert
        assertThatThrownBy(() -> ItemCuantitativoAsesorDomain.crear(nombre, descripcion, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.NOMBRE,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.NOMBRE_DEMASIADO_LARGO),
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_REQUERIDA),
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.CATEGORIA,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.CATEGORIA_REQUERIDA),
                                        tuple(EvaluacionesFields.ItemCuantitativoAsesor.VALOR,
                                                EvaluacionesCodes.ItemCuantitativoAsesor.VALOR_REQUERIDO)));
    }

    @Test
    void debeAceptarLimitesYRechazarFuera_cuandoValorEsExtremo() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> ItemCuantitativoAsesorDomain.crear("a", "b", categoria, 0)).doesNotThrowAnyException();
        assertThatCode(() -> ItemCuantitativoAsesorDomain.crear("a", "b", categoria, 500)).doesNotThrowAnyException();
        assertThatThrownBy(() -> ItemCuantitativoAsesorDomain.crear("a", "b", categoria, -1))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ItemCuantitativoAsesor.VALOR_FUERA_DE_RANGO));
        assertThatThrownBy(() -> ItemCuantitativoAsesorDomain.crear("a", "b", categoria, 501))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void debeReconstruirSinValidar_cuandoSeCargaDesdePersistencia() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var categoria = UtilUUID.generarNuevoUUID();

        // Act
        var item = ItemCuantitativoAsesorDomain.reconstruir(id, "", "  x ", categoria, 9999);

        // Assert
        assertThat(item.getId()).isEqualTo(id);
        assertThat(item.getNombre()).isEmpty();
        assertThat(item.getDescripcion()).isEqualTo("  x ");
        assertThat(item.getCategoria()).isEqualTo(categoria);
        assertThat(item.getValor()).isEqualTo(9999);
    }
}
