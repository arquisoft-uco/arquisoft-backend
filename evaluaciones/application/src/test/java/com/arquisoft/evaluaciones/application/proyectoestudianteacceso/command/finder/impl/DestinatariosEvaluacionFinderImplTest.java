package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.finder.impl;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.ContactoEstudianteOutputPort;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.ProyectoEstudianteAccesoOutputPort;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.DestinatarioEvaluacionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DestinatariosEvaluacionFinderImplTest {

    @Mock
    private ProyectoEstudianteAccesoOutputPort proyectoEstudianteAccesoOutputPort;

    @Mock
    private ContactoEstudianteOutputPort contactoEstudianteOutputPort;

    @InjectMocks
    private DestinatariosEvaluacionFinderImpl finder;

    @Test
    void debeComponerAmbosPuertos_cuandoObtieneDestinatariosDelEntregable() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        Set<UUID> estudiantes = Set.of(estudiante);
        List<DestinatarioEvaluacionEntity> contactos = List.of(
                new DestinatarioEvaluacionEntity(estudiante, "estudiante@uco.edu.co"));
        when(proyectoEstudianteAccesoOutputPort.obtenerEstudiantesConAccesoPorEntregable(entregable))
                .thenReturn(estudiantes);
        when(contactoEstudianteOutputPort.consultarContactos(estudiantes)).thenReturn(contactos);

        // Act
        List<DestinatarioEvaluacionEntity> resultado = finder.obtener(entregable);

        // Assert
        assertThat(resultado).isEqualTo(contactos);
        verify(proyectoEstudianteAccesoOutputPort).obtenerEstudiantesConAccesoPorEntregable(entregable);
        verify(contactoEstudianteOutputPort).consultarContactos(estudiantes);
    }
}
