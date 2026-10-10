package com.arquisoft.proyectos.domain.estudianteproyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregacionEstudiantesProyectoGradoDomainTest {

    @Test
    void debeExponerProyectoCoordinadorEstudiantesYCantidad_cuandoLasRelacionesSonValidas() {
        // Arrange
        var proyectoGrado = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID());
        var relaciones = EstudianteProyectoGradoDomain.crear(proyectoGrado, estudiantes);

        // Act
        var agregacion = AgregacionEstudiantesProyectoGradoDomain.crear(relaciones, coordinador);

        // Assert
        assertThat(agregacion.getRelaciones()).containsExactlyElementsOf(relaciones);
        assertThat(agregacion.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(agregacion.getCoordinador()).isEqualTo(coordinador);
        assertThat(agregacion.getEstudiantes()).containsExactlyElementsOf(estudiantes);
        assertThat(agregacion.getCantidad()).isEqualTo(2);
    }

    @Test
    void debeLanzarExcepcion_cuandoLasRelacionesLleganNulas() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionEstudiantesProyectoGradoDomain.crear(null, UUID.randomUUID()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS)));
    }

    @Test
    void debeLanzarExcepcion_cuandoElCoordinadorLlegaNulo() {
        // Arrange
        var relaciones = EstudianteProyectoGradoDomain.crear(UUID.randomUUID(), List.of(UUID.randomUUID()));

        // Act & Assert
        assertThatThrownBy(() -> AgregacionEstudiantesProyectoGradoDomain.crear(relaciones, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(ProyectosFields.EstudianteProyectoGrado.COORDINADOR);
                            assertThat(error.codigoError())
                                    .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.COORDINADOR_ID_REQUERIDO);
                        }));
    }
}
