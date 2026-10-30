package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
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
class ArtefactoOmitidoDelRepositorioFinderImplTest {

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @InjectMocks
    private ArtefactoOmitidoDelRepositorioFinderImpl finder;

    @Test
    void debeDevolverElOmitido_cuandoElProyectoTieneUnoDelMismoRepositorio() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var nuevo = ArtefactoDomain.crear(proyecto, UtilUUID.generarNuevoUUID(), version);
        var omitidoEn = UtilFecha.generarInstanteActual();
        var idOmitido = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerOmitidoPorProyectoYRepositorio(proyecto, version)).thenReturn(Optional.of(
                new ArtefactoEntity(idOmitido, proyecto, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                        omitidoEn)));

        // Act
        var resultado = finder.obtener(nuevo);

        // Assert
        assertThat(resultado.getId()).isEqualTo(idOmitido);
        assertThat(resultado.estaOmitido()).isTrue();
    }

    @Test
    void debeDevolverVacio_cuandoNoHayArtefactoOmitido() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var nuevo = ArtefactoDomain.crear(proyecto, UtilUUID.generarNuevoUUID(), version);
        when(artefactoOutputPort.obtenerOmitidoPorProyectoYRepositorio(proyecto, version))
                .thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(nuevo);

        // Assert
        assertThat(resultado).isSameAs(ArtefactoDomain.VACIO);
    }
}
