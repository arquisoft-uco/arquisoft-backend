package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class ConsultarMapaRutaProyectoGradoAsesorQueryTest {

    @Test
    void debeCrearLaConsulta_cuandoElProyectoGradoEsValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarMapaRutaProyectoGradoAsesorQuery.crear(proyectoGrado);

        // Assert
        assertThat(query.proyectoGrado()).isEqualTo(proyectoGrado);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoElProyectoGradoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarMapaRutaProyectoGradoAsesorQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::campo, ValidationError::codigoError)
                                .containsExactly(tuple(MapasRutaFields.MapaRuta.PROYECTO_GRADO,
                                        MapasRutaCodes.MapaRuta.PROYECTO_GRADO_REQUERIDO)));
    }
}
