package com.arquisoft.proyectos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestitucionEstudianteProyectoGradoDomainTest {

    @Test
    void debeExponerLosTresIds_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var destitucion = DestitucionEstudianteProyectoGradoDomain.crear(proyectoGrado, estudiante, coordinador);

        // Assert
        assertThat(destitucion.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(destitucion.getEstudiante()).isEqualTo(estudiante);
        assertThat(destitucion.getCoordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosTresLlegaNulos() {
        // Act & Assert
        assertThatThrownBy(() -> DestitucionEstudianteProyectoGradoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_REQUERIDO,
                                ProyectosCodes.EstudianteProyectoGrado.COORDINADOR_ID_REQUERIDO));
    }
}
