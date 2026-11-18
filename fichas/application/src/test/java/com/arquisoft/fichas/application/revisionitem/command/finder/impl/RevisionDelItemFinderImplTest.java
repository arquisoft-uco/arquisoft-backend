package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.RevisionItemDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevisionDelItemFinderImplTest {

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @InjectMocks
    private RevisionDelItemFinderImpl finder;

    @Test
    void debeRetornarLaRevisionMapeada_cuandoElItemTieneRevision() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var revisionId = UtilUUID.generarNuevoUUID();
        when(revisionItemOutputPort.buscarPorItem(item))
                .thenReturn(Optional.of(new RevisionItemEntity(revisionId, item, "VISUALIZADA", Instant.now())));

        // Act
        var resultado = finder.obtener(item);

        // Assert
        assertThat(resultado.getId()).isEqualTo(revisionId);
        assertThat(resultado.getEstadoRevision()).isEqualTo(EstadoRevision.VISUALIZADA);
        verify(revisionItemOutputPort, times(1)).buscarPorItem(item);
    }

    @Test
    void debeRetornarVacio_cuandoElItemNoTieneRevision() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        when(revisionItemOutputPort.buscarPorItem(item)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(item);

        // Assert
        assertThat(resultado).isSameAs(RevisionItemDomain.VACIO);
    }
}
