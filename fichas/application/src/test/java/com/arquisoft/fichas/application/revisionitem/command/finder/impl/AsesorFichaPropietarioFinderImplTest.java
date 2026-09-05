package com.arquisoft.fichas.application.revisionitem.command.finder.impl;

import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.RevisionItemDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorFichaPropietarioFinderImplTest {

    @Mock
    private FichaPerfilDelItemFinder fichaPerfilDelItemFinder;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @InjectMocks
    private AsesorFichaPropietarioFinderImpl finder;

    private final UUID asesorFicha = UUID.randomUUID();
    private final UUID fichaPerfilId = UUID.randomUUID();

    @Test
    void debeRetornarTrue_cuandoElAsesorEsPropietarioDeLaFichaDelItem() {
        // Arrange
        var entrada = agregacionCon(asesorFicha);
        var ficha = FichaPerfilDomain.crear("Título de prueba", asesorFicha);
        when(fichaPerfilDelItemFinder.obtener(entrada.getItem())).thenReturn(Optional.of(fichaPerfilId));
        when(fichaPerfilFinder.obtener(fichaPerfilId)).thenReturn(Optional.of(ficha));

        // Act
        Boolean resultado = finder.obtener(entrada);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElAsesorNoEsPropietarioDeLaFichaDelItem() {
        // Arrange
        var entrada = agregacionCon(asesorFicha);
        var fichaDeOtroAsesor = FichaPerfilDomain.crear("Otro título", UUID.randomUUID());
        when(fichaPerfilDelItemFinder.obtener(entrada.getItem())).thenReturn(Optional.of(fichaPerfilId));
        when(fichaPerfilFinder.obtener(fichaPerfilId)).thenReturn(Optional.of(fichaDeOtroAsesor));

        // Act
        Boolean resultado = finder.obtener(entrada);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    void debeRetornarFalse_cuandoLaFichaDelItemNoExiste() {
        // Arrange — degrada a false sin necesidad de consultar el fichaPerfilFinder
        var entrada = agregacionCon(asesorFicha);
        when(fichaPerfilDelItemFinder.obtener(entrada.getItem())).thenReturn(Optional.empty());

        // Act
        Boolean resultado = finder.obtener(entrada);

        // Assert
        assertThat(resultado).isFalse();
        verify(fichaPerfilFinder, never()).obtener(any());
    }

    private static AgregacionRevisionItemDomain agregacionCon(UUID asesorFicha) {
        var revisionItem = RevisionItemDomain.crear(UUID.randomUUID());
        return AgregacionRevisionItemDomain.crear(revisionItem, asesorFicha);
    }
}
