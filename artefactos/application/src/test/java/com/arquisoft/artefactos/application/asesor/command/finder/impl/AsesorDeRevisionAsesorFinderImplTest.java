package com.arquisoft.artefactos.application.asesor.command.finder.impl;

import com.arquisoft.artefactos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.artefactos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class AsesorDeRevisionAsesorFinderImplTest {

    @Mock
    private AsesorOutputPort outputPort;

    @InjectMocks
    private AsesorDeRevisionAsesorFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoExiste() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorRevisionAsesor(revision))
                .thenReturn(Optional.of(new AsesorEntity(id, "20161020123", "Ana", "ana@uco.edu.co", Instant.now(), null)));

        // Act
        var resultado = finder.obtener(revision);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getNombre()).isEqualTo("Ana");
        assertThat(resultado.getEmail()).isEqualTo("ana@uco.edu.co");
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorRevisionAsesor(revision)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(revision);

        // Assert
        assertThat(resultado).isSameAs(AsesorDomain.VACIO);
    }
}
