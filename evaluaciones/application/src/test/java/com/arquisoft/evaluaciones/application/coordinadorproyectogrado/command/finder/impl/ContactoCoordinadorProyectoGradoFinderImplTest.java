package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.impl;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.CoordinadorProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.entity.CoordinadorProyectoGradoEntity;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactoCoordinadorProyectoGradoFinderImplTest {

    @Mock
    private CoordinadorProyectoGradoOutputPort outputPort;

    @InjectMocks
    private ContactoCoordinadorProyectoGradoFinderImpl finder;

    @Test
    void debeDevolverContacto_cuandoLaReplicaTieneCoordinador() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorProyectoGrado(proyectoGrado)).thenReturn(Optional.of(new CoordinadorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), coordinador, "Coord", "coord@uco.edu.co", UtilFecha.generarInstanteActual())));

        // Act
        var contacto = finder.obtener(proyectoGrado);

        // Assert
        assertThat(contacto).isEqualTo(new ContactoCoordinador(coordinador, "Coord", "coord@uco.edu.co"));
    }

    @Test
    void debeDevolverVacio_cuandoNoHayCoordinador() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorProyectoGrado(proyectoGrado)).thenReturn(Optional.empty());

        // Act
        var contacto = finder.obtener(proyectoGrado);

        // Assert
        assertThat(contacto).isSameAs(ContactoCoordinador.VACIO);
    }
}
