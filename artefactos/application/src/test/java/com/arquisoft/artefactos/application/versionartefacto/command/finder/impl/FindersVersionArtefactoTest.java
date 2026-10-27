package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AnalisisArchivoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.AnalisisArchivoEntity;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.CargaPendienteEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindersVersionArtefactoTest {

    @Mock
    private AlmacenamientoVersionOutputPort almacenamiento;
    @Mock
    private AnalisisArchivoOutputPort analisisOutputPort;

    @Test
    void debeRetornarCargaPendiente_cuandoExiste() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        when(almacenamiento.obtenerCargaPendiente(carga)).thenReturn(Optional.of(
                new CargaPendienteEntity(carga, artefacto, solicitante, "a.pdf", "application/pdf", 10L, "etag")));

        // Act
        var resultado = new CargaPendienteFinderImpl(almacenamiento).obtener(carga);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getCarga()).isEqualTo(carga);
        assertThat(resultado.getArtefacto()).isEqualTo(artefacto);
        assertThat(resultado.getSolicitante()).isEqualTo(solicitante);
        assertThat(resultado.getEtag()).isEqualTo("etag");
    }

    @Test
    void debeRetornarVacio_cuandoNoHayCargaPendiente() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(almacenamiento.obtenerCargaPendiente(carga)).thenReturn(Optional.empty());

        // Act
        var resultado = new CargaPendienteFinderImpl(almacenamiento).obtener(carga);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }

    @Test
    void debeRetornarAnalisis_cuandoExiste() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(analisisOutputPort.analizarCarga(carga))
                .thenReturn(Optional.of(new AnalisisArchivoEntity("application/pdf", "etag")));

        // Act
        var resultado = new AnalisisCargaFinderImpl(analisisOutputPort).obtener(carga);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getTipoDetectado()).isEqualTo("application/pdf");
        assertThat(resultado.getEtag()).isEqualTo("etag");
    }

    @Test
    void debeRetornarVacio_cuandoNoHayAnalisis() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(analisisOutputPort.analizarCarga(carga)).thenReturn(Optional.empty());

        // Act
        var resultado = new AnalisisCargaFinderImpl(analisisOutputPort).obtener(carga);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
