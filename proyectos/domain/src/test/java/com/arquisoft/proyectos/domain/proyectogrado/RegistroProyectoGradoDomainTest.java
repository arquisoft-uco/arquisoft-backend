package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistroProyectoGradoDomainTest {

    @Test
    void debeComponerProyectoYEstudiantes_cuandoAmbasPartesEstanPresentes() {
        // Arrange
        var proyecto = ProyectoGradoDomain.crear(UUID.randomUUID(), "Titulo", UUID.randomUUID());
        var estudiantes = AgregacionEstudiantesProyectoGradoDomain.crear(
                EstudianteProyectoGradoDomain.crear(proyecto.getId(), List.of(UUID.randomUUID())),
                proyecto.getCoordinador());
        var estadoInicial = EstadoProyectoGradoDomain.crear(proyecto.getId());

        // Act
        var registro = RegistroProyectoGradoDomain.crear(proyecto, estadoInicial, estudiantes);

        // Assert
        assertThat(registro.getProyecto()).isSameAs(proyecto);
        assertThat(registro.getEstadoInicial()).isSameAs(estadoInicial);
        assertThat(registro.getEstudiantes()).isSameAs(estudiantes);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodasLasPartesSonNulas() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroProyectoGradoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.ProyectoGrado.PROYECTO);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.EstadoProyectoGrado.ESTADO_INICIAL);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.EstadoProyectoGrado.ESTADO_INICIAL_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS);
                    });
                });
    }
}
