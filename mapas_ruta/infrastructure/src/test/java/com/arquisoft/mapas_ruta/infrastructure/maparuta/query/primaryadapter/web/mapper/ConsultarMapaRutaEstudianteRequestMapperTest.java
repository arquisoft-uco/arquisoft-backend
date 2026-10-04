package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarMapaRutaEstudianteRequestMapperTest {

    @Test
    void debeCrearLaConsultaConElEstudiante_cuandoElUuidEsValido() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarMapaRutaEstudianteRequestMapper.toQuery(estudiante);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeLanzarExcepcionDeEntrada_cuandoElUuidEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarMapaRutaEstudianteRequestMapper.toQuery(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(MapasRutaCodes.MapaRuta.ESTUDIANTE_REQUERIDO));
    }
}
