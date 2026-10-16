package com.arquisoft.mapas_ruta.domain.maparuta;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class MapaRutaDomainTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 10, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 1);

    @Test
    void debeCrearMapaRutaConId_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var mapaRuta = MapaRutaDomain.crear(proyectoGrado, INICIO, FIN);

        // Assert
        assertThat(mapaRuta.getId()).isNotNull();
        assertThat(mapaRuta.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(mapaRuta.getFechaInicio()).isEqualTo(INICIO);
        assertThat(mapaRuta.getFechaFin()).isEqualTo(FIN);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoProyectoYFechasSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> MapaRutaDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        tuple(MapasRutaFields.MapaRuta.PROYECTO_GRADO,
                                                MapasRutaCodes.MapaRuta.PROYECTO_GRADO_REQUERIDO),
                                        tuple(MapasRutaFields.MapaRuta.FECHA_INICIO,
                                                MapasRutaCodes.MapaRuta.FECHA_INICIO_REQUERIDA),
                                        tuple(MapasRutaFields.MapaRuta.FECHA_FIN,
                                                MapasRutaCodes.MapaRuta.FECHA_FIN_REQUERIDA)));
    }

    @Test
    void debeRechazarLaFechaFin_cuandoEsIgualOAnteriorALaFechaInicio() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> MapaRutaDomain.crear(proyectoGrado, INICIO, INICIO))
                .isInstanceOfSatisfying(DomainValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .singleElement()
                                .satisfies(error -> {
                                    assertThat(error.campo()).isEqualTo(MapasRutaFields.MapaRuta.FECHA_FIN);
                                    assertThat(error.codigoError())
                                            .isEqualTo(MapasRutaCodes.MapaRuta.FECHA_FIN_NO_POSTERIOR);
                                }));
        assertThatThrownBy(() -> MapaRutaDomain.crear(proyectoGrado, INICIO, INICIO.minusDays(1)))
                .isInstanceOf(DomainValidationException.class);
    }
}
