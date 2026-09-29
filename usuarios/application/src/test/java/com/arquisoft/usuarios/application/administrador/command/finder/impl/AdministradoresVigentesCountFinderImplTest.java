package com.arquisoft.usuarios.application.administrador.command.finder.impl;

import com.arquisoft.usuarios.application.administrador.command.secondaryport.AdministradorOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdministradoresVigentesCountFinderImplTest {

    @Mock
    private AdministradorOutputPort administradorOutputPort;

    @InjectMocks
    private AdministradoresVigentesCountFinderImpl finder;

    @Test
    void debeDevolverElConteoDelPuerto_cuandoSeConsulta() {
        // Arrange
        when(administradorOutputPort.contarVigentes()).thenReturn(3L);

        // Act
        var resultado = finder.obtener();

        // Assert
        assertThat(resultado).isEqualTo(3L);
    }
}
