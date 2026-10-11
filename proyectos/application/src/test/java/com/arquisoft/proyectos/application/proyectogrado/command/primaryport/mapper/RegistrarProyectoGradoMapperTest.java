package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.RegistrarProyectoGradoCommand;
import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarProyectoGradoMapperTest {

    @Test
    void debeVincularLosEstudiantesAlIdDelProyectoCreado_cuandoSeConstruyeElRegistro() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID());
        var command = new RegistrarProyectoGradoCommand(fichaPerfil, "Sistema de gestión", coordinador, estudiantes);

        // Act
        var registro = RegistrarProyectoGradoMapper.toDomain(command);

        // Assert
        var proyecto = registro.getProyecto();
        assertThat(proyecto.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(proyecto.getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(proyecto.getCoordinador()).isEqualTo(coordinador);
        assertThat(proyecto.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.EN_PROCESO);
        assertThat(registro.getEstudiantes().getProyectoGrado()).isEqualTo(proyecto.getId());
        assertThat(registro.getEstudiantes().getCoordinador()).isEqualTo(coordinador);
        assertThat(registro.getEstudiantes().getEstudiantes()).containsExactlyElementsOf(estudiantes);
    }
}
