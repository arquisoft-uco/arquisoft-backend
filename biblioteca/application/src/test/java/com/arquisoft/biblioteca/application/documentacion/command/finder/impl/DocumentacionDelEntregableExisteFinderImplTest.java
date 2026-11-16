package com.arquisoft.biblioteca.application.documentacion.command.finder.impl;

import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.DocumentacionOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentacionDelEntregableExisteFinderImplTest {

    @Mock
    private DocumentacionOutputPort documentacionOutputPort;

    @InjectMocks
    private DocumentacionDelEntregableExisteFinderImpl finder;

    @Test
    void debeRetornarLoQueDiceElPuerto_cuandoConsultaPorEntregable() {
        // Arrange
        var con = UtilUUID.generarNuevoUUID();
        var sin = UtilUUID.generarNuevoUUID();
        when(documentacionOutputPort.existePorEntregableProyectoGrado(con)).thenReturn(true);
        when(documentacionOutputPort.existePorEntregableProyectoGrado(sin)).thenReturn(false);

        // Act
        var existe = finder.obtener(con);
        var noExiste = finder.obtener(sin);

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
