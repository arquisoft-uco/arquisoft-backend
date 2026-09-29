package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.AsesoriaRevisionItemEntity;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.model.AsesoriaRevisionItem;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesoriaRevisionItemFinderImplTest {

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @InjectMocks
    private AsesoriaRevisionItemFinderImpl finder;

    @Test
    void debeRetornarLaAsesoriaMapeada_cuandoLaRevisionExiste() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        when(revisionItemOutputPort.obtenerAsesoria(revisionItem))
                .thenReturn(Optional.of(new AsesoriaRevisionItemEntity(fichaPerfil, asesorFicha, "VISUALIZADA")));

        // Act
        var resultado = finder.obtener(revisionItem);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(resultado.asesorFicha()).isEqualTo(asesorFicha);
        assertThat(resultado.estadoRevision()).isEqualTo(EstadoRevision.VISUALIZADA);
        verify(revisionItemOutputPort, times(1)).obtenerAsesoria(revisionItem);
    }

    @Test
    void debeRetornarVacio_cuandoLaRevisionNoExiste() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        when(revisionItemOutputPort.obtenerAsesoria(revisionItem)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(revisionItem);

        // Assert
        assertThat(resultado).isSameAs(AsesoriaRevisionItem.VACIO);
        verify(revisionItemOutputPort, times(1)).obtenerAsesoria(revisionItem);
    }
}
