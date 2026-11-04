package com.arquisoft.fichas.application.observacionitem.command.finder.impl;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.domain.observacionitem.ModificacionObservacionItemDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtrasObservacionesIgualesEnRevisionFinderImplTest {

    @Mock
    private ObservacionItemOutputPort observacionItemOutputPort;

    @InjectMocks
    private OtrasObservacionesIgualesEnRevisionFinderImpl finder;

    @Test
    void debeDelegarAlPuertoConLaObservacionYSuTexto_cuandoSeConsulta() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var entrada = ModificacionObservacionItemDomain.crear(
                observacionItem, "Observación válida", UtilUUID.generarNuevoUUID());
        when(observacionItemOutputPort.contarOtrasIgualesEnRevision(observacionItem, "Observación válida"))
                .thenReturn(2L);

        // Act
        var resultado = finder.obtener(entrada);

        // Assert
        assertThat(resultado).isEqualTo(2L);
        verify(observacionItemOutputPort).contarOtrasIgualesEnRevision(observacionItem, "Observación válida");
    }
}
