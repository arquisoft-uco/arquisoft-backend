package com.arquisoft.proyectos.application.estudiante.command.finder.impl;

import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesVigentesDeProyectoGradoFinderImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudiantesVigentesDeProyectoGradoFinderImpl finder;

    @Test
    void debeMapearLosEstudiantesAlDominio_cuandoElProyectoTieneVigentes() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var entity = new EstudianteEntity(id, "2020", "Ana Ruiz", "ana.ruiz@soyuco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), UtilFecha.VACIO);
        when(estudianteOutputPort.obtenerVigentesPorProyectoGrado(proyectoGrado)).thenReturn(List.of(entity));

        // Act
        var resultado = finder.obtener(proyectoGrado);

        // Assert
        assertThat(resultado).singleElement().satisfies(estudiante -> {
            assertThat(estudiante.getId()).isEqualTo(id);
            assertThat(estudiante.getNombre()).isEqualTo("Ana Ruiz");
            assertThat(estudiante.getEmail()).isEqualTo("ana.ruiz@soyuco.edu.co");
        });
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayVigentes() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(estudianteOutputPort.obtenerVigentesPorProyectoGrado(proyectoGrado)).thenReturn(List.of());

        // Act
        var resultado = finder.obtener(proyectoGrado);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
