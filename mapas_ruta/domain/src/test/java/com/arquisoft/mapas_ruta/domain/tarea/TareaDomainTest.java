package com.arquisoft.mapas_ruta.domain.tarea;

import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TareaDomainTest {

    @Test
    void debeCrearTarea_cuandoDatosValidos() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var tarea = TareaDomain.crear(mapaRuta, "  Redactar el capitulo uno  ", asesor);

        // Assert
        assertThat(tarea.getId()).isNotNull();
        assertThat(tarea.getMapaRuta()).isEqualTo(mapaRuta);
        assertThat(tarea.getAsesor()).isEqualTo(asesor);
        assertThat(tarea.getDescripcion()).isEqualTo("Redactar el capitulo uno");
        assertThat(tarea.getEstadoItem()).isEqualTo(EstadoItem.NUEVA);
    }

    @Test
    void debeAcumularFieldErrors_cuandoMapaDescripcionYAsesorInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> TareaDomain.crear(null, "   ", null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.Tarea.MAPA_RUTA_REQUERIDO,
                                        MapasRutaCodes.Tarea.DESCRIPCION_REQUERIDA,
                                        MapasRutaCodes.Tarea.ASESOR_REQUERIDO));
    }

    @Test
    void debeRechazar_cuandoDescripcionSuperaElMaximo() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var descripcion = "a".repeat(201);

        // Act & Assert
        assertThatThrownBy(() -> TareaDomain.crear(mapaRuta, descripcion, asesor))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.Tarea.DESCRIPCION_DEMASIADO_LARGA));
    }

    @Test
    void debeReconstruir_conTodosLosCamposSinValidar() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var tarea = TareaDomain.reconstruir(id, mapaRuta, "", asesor, EstadoItem.FINALIZADA);

        // Assert
        assertThat(tarea.getId()).isEqualTo(id);
        assertThat(tarea.getMapaRuta()).isEqualTo(mapaRuta);
        assertThat(tarea.getDescripcion()).isEmpty();
        assertThat(tarea.getAsesor()).isEqualTo(asesor);
        assertThat(tarea.getEstadoItem()).isEqualTo(EstadoItem.FINALIZADA);
        assertThat(tarea.esVacio()).isFalse();
    }

    @Test
    void debeSerVacio_elCentinela() {
        // Act & Assert
        assertThat(TareaDomain.VACIO.esVacio()).isTrue();
        assertThat(TareaDomain.VACIO.getEstadoItem()).isEqualTo(EstadoItem.VACIO);
        assertThat(UtilUUID.esPorDefecto(TareaDomain.VACIO.getId())).isTrue();
    }

    @Test
    void debeAceptar_cuandoDescripcionTieneExactamenteElMaximoTrasRecortar() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var descripcion = " " + "a".repeat(200) + " ";

        // Act & Assert
        assertThatCode(() -> TareaDomain.crear(mapaRuta, descripcion, asesor)).doesNotThrowAnyException();
    }
}
