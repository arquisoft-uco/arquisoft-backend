package com.arquisoft.evaluaciones.application.jurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JuradosVigentesFinderImplTest {

    @Mock
    private JuradoOutputPort outputPort;

    @InjectMocks
    private JuradosVigentesFinderImpl finder;

    @Test
    void debeDevolverLosVigentesDelPuerto_cuandoSeConsultan() {
        // Arrange
        var vigente = UtilUUID.generarNuevoUUID();
        var removido = UtilUUID.generarNuevoUUID();
        var solicitados = Set.of(vigente, removido);
        var vigentes = Set.of(vigente);
        when(outputPort.consultarVigentesPorIds(solicitados)).thenReturn(vigentes);

        // Act
        var resultado = finder.obtener(solicitados);

        // Assert
        assertThat(resultado).isSameAs(vigentes);
    }
}
