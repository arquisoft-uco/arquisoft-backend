package com.arquisoft.entregables.application.artefacto.command.finder.impl;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
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
class ArtefactoPorIdFinderImplTest {

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @InjectMocks
    private ArtefactoPorIdFinderImpl finder;

    @Test
    void debeDevolverElArtefactoMapeado_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var entity = new ArtefactoEntity(id, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 2, Instant.now(),
                "clave", UtilUUID.generarNuevoUUID(), "Documento", UtilUUID.generarNuevoUUID(), "Repo", null, null);
        when(artefactoOutputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var artefacto = finder.obtener(id);

        // Assert
        assertThat(artefacto.getId()).isEqualTo(id);
        assertThat(artefacto.getNumeroVersion()).isEqualTo(2);
        assertThat(artefacto.getClaveObjeto()).isEqualTo("clave");
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var artefacto = finder.obtener(id);

        // Assert
        assertThat(artefacto).isSameAs(ArtefactoDomain.VACIO);
    }
}
