package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
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
class RevisionAsesorFinderImplTest {

    @Mock
    private RevisionAsesorOutputPort outputPort;

    @InjectMocks
    private RevisionAsesorFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-10-28T10:00:00Z");
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.of(new RevisionAsesorEntity(id, version, "RESUELTA", fecha)));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getVersionArtefacto()).isEqualTo(version);
        assertThat(resultado.getEstado()).isEqualTo(EstadoRevisionAsesor.RESUELTA);
        assertThat(resultado.getFechaCreacion()).isEqualTo(fecha);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(RevisionAsesorDomain.VACIO);
    }
}
