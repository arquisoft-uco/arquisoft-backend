package com.arquisoft.evaluaciones.application.asesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.evaluaciones.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.evaluaciones.domain.asesor.model.ContactoAsesor;
import com.arquisoft.shared.util.UtilFecha;
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
class ContactoAsesorEvaluacionFinderImplTest {

    @Mock
    private AsesorOutputPort outputPort;

    @InjectMocks
    private ContactoAsesorEvaluacionFinderImpl finder;

    @Test
    void debeDevolverElContacto_cuandoElAsesorExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerContactoPorEvaluacion(evaluacion)).thenReturn(Optional.of(
                new AsesorEntity(asesor, "ID", "Asesor", "asesor@uco.edu.co", Instant.now(), UtilFecha.VACIO)));

        // Act
        var contacto = finder.obtener(evaluacion);

        // Assert
        assertThat(contacto).isEqualTo(new ContactoAsesor(asesor, "Asesor", "asesor@uco.edu.co"));
    }

    @Test
    void debeDevolverVacio_cuandoNoHayAsesorParaLaEvaluacion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerContactoPorEvaluacion(evaluacion)).thenReturn(Optional.empty());

        // Act
        var contacto = finder.obtener(evaluacion);

        // Assert
        assertThat(contacto).isSameAs(ContactoAsesor.VACIO);
    }
}
