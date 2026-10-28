package com.arquisoft.mapas_ruta.domain.tarea;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificacionTareaDomainTest {

    @Test
    void debeCrearConDescripcionRecortada_cuandoDatosValidos() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var modificacion = ModificacionTareaDomain.crear(tarea, "  Redactar capitulo  ", asesor);

        // Assert
        assertThat(modificacion.getTarea()).isEqualTo(tarea);
        assertThat(modificacion.getDescripcion()).isEqualTo("Redactar capitulo");
        assertThat(modificacion.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoTodoEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> ModificacionTareaDomain.crear(null, "   ", null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.Tarea.TAREA_REQUERIDA,
                                        MapasRutaCodes.Tarea.DESCRIPCION_REQUERIDA,
                                        MapasRutaCodes.Tarea.ASESOR_REQUERIDO));
    }

    @Test
    void debeFallar_cuandoDescripcionSuperaMaximo() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var descripcion = "a".repeat(201);

        // Act & Assert
        assertThatThrownBy(() -> ModificacionTareaDomain.crear(tarea, descripcion, asesor))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.Tarea.DESCRIPCION_DEMASIADO_LARGA));
    }
}
