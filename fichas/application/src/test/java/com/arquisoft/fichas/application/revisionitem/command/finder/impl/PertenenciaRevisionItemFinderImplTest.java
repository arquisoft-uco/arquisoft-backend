package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.PertenenciaRevisionItemEntity;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.VisualizacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.model.PertenenciaRevisionItem;
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
class PertenenciaRevisionItemFinderImplTest {

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @InjectMocks
    private PertenenciaRevisionItemFinderImpl finder;

    @Test
    void debeRetornarLaPertenenciaMapeada_cuandoLaRevisionExiste() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var entrada = VisualizacionRevisionItemDomain.crear(revisionItem, estudiante);
        when(revisionItemOutputPort.obtenerPertenencia(revisionItem, estudiante))
                .thenReturn(Optional.of(new PertenenciaRevisionItemEntity(fichaPerfil, true, "EN_PROGRESO")));

        // Act
        var resultado = finder.obtener(entrada);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(resultado.esPropietario()).isTrue();
        assertThat(resultado.estadoRevision()).isEqualTo(EstadoRevision.EN_PROGRESO);
        verify(revisionItemOutputPort, times(1)).obtenerPertenencia(revisionItem, estudiante);
    }

    @Test
    void debeRetornarVacio_cuandoLaRevisionNoExiste() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var entrada = VisualizacionRevisionItemDomain.crear(revisionItem, estudiante);
        when(revisionItemOutputPort.obtenerPertenencia(revisionItem, estudiante))
                .thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(entrada);

        // Assert
        assertThat(resultado).isSameAs(PertenenciaRevisionItem.VACIO);
        verify(revisionItemOutputPort, times(1)).obtenerPertenencia(revisionItem, estudiante);
    }
}
