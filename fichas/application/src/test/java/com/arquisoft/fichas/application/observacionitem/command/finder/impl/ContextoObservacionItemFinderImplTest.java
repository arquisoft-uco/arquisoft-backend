package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ContextoObservacionItemEntity;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
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
class ContextoObservacionItemFinderImplTest {

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @InjectMocks
    private ContextoObservacionItemFinderImpl finder;

    @Test
    void debeDevolverElContextoMapeado_cuandoLaObservacionExiste() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var revisionItem = UtilUUID.generarNuevoUUID();
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        when(observacionItemOutputPort.obtenerContexto(observacionItem)).thenReturn(Optional.of(
                new ContextoObservacionItemEntity(revisionItem, "EN_PROGRESO", fichaPerfil, asesorFicha)));

        // Act
        var resultado = finder.obtener(observacionItem);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.revisionItem()).isEqualTo(revisionItem);
        assertThat(resultado.estadoRevision()).isEqualTo(EstadoRevision.EN_PROGRESO);
        assertThat(resultado.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(resultado.asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeDevolverVacio_cuandoLaObservacionNoExiste() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        when(observacionItemOutputPort.obtenerContexto(observacionItem)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(observacionItem);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
