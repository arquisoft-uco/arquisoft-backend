package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.impl;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.CoordinadorProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.entity.CoordinadorProyectoGradoEntity;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactoCoordinadorEvaluacionFinderImplTest {

    @Mock
    private CoordinadorProyectoGradoOutputPort outputPort;

    @InjectMocks
    private ContactoCoordinadorEvaluacionFinderImpl finder;

    @Test
    void debeDevolverElContacto_cuandoLaReplicaExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorEvaluacion(evaluacion)).thenReturn(Optional.of(new CoordinadorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), coordinador, "Coord", "coord@uco.edu.co", Instant.now())));

        // Act
        var contacto = finder.obtener(evaluacion);

        // Assert
        assertThat(contacto).isEqualTo(new ContactoCoordinador(coordinador, "Coord", "coord@uco.edu.co"));
    }

    @Test
    void debeDevolverVacio_cuandoNoHayReplicaDelCoordinador() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorEvaluacion(evaluacion)).thenReturn(Optional.empty());

        // Act
        var contacto = finder.obtener(evaluacion);

        // Assert
        assertThat(contacto).isSameAs(ContactoCoordinador.VACIO);
    }
}
