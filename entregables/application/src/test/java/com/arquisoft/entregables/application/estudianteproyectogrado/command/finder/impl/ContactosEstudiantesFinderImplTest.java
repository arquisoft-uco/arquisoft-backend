package com.arquisoft.entregables.application.estudianteproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.entregables.domain.estudianteproyectogrado.model.ContactoEstudiante;
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
class ContactosEstudiantesFinderImplTest {

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;

    @InjectMocks
    private ContactosEstudiantesProyectoGradoFinderImpl porProyecto;

    @InjectMocks
    private ContactosEstudiantesEntregableFinderImpl porEntregable;

    private static EstudianteProyectoGradoEntity vinculo(String nombre, String email) {
        return new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), nombre, email, Instant.parse("2026-11-19T10:00:00Z"), UtilFecha.VACIO);
    }

    @Test
    void debeMapearAContactos_cuandoElProyectoTieneEstudiantesVigentes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var ana = vinculo("Ana", "ana@uco.edu.co");
        var luis = vinculo("Luis", "luis@uco.edu.co");
        when(estudianteProyectoGradoOutputPort.consultarContactosVigentesPorProyectoGrado(proyecto))
                .thenReturn(List.of(ana, luis));

        // Act
        var contactos = porProyecto.obtener(proyecto);

        // Assert
        assertThat(contactos).containsExactly(
                new ContactoEstudiante(ana.estudiante(), "Ana", "ana@uco.edu.co"),
                new ContactoEstudiante(luis.estudiante(), "Luis", "luis@uco.edu.co"));
    }

    @Test
    void debeMapearAContactos_cuandoElEntregableTieneEstudiantesVigentes() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var ana = vinculo("Ana", "ana@uco.edu.co");
        when(estudianteProyectoGradoOutputPort.consultarContactosVigentesPorEntregable(entregable))
                .thenReturn(List.of(ana));

        // Act
        var contactos = porEntregable.obtener(entregable);

        // Assert
        assertThat(contactos).containsExactly(new ContactoEstudiante(ana.estudiante(), "Ana", "ana@uco.edu.co"));
    }

    @Test
    void debeDevolverListaVacia_cuandoNoHayFilas() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(estudianteProyectoGradoOutputPort.consultarContactosVigentesPorProyectoGrado(id)).thenReturn(List.of());
        when(estudianteProyectoGradoOutputPort.consultarContactosVigentesPorEntregable(id)).thenReturn(List.of());

        // Act & Assert
        assertThat(porProyecto.obtener(id)).isEmpty();
        assertThat(porEntregable.obtener(id)).isEmpty();
    }
}
