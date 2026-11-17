package com.arquisoft.seguridad.application.autorizacion.command.finder.impl;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.PoliticaRecursoOutputPort;
import com.arquisoft.seguridad.domain.autorizacion.AccesoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecursoSoloAutenticacionFinderImplTest {

    @Mock
    private PoliticaRecursoOutputPort politicaRecursoOutputPort;

    @InjectMocks
    private RecursoSoloAutenticacionFinderImpl finder;

    @Test
    void debeDelegarMetodoYRecursoAlPuerto_cuandoSeConsulta() {
        // Arrange
        var acceso = AccesoDomain.crear("GET", "/api/usuarios/mi/perfil", "token", "principal", "corr");
        when(politicaRecursoOutputPort.exigeSoloAutenticacion("GET", "/api/usuarios/mi/perfil")).thenReturn(true);

        // Act
        var resultado = finder.obtener(acceso);

        // Assert
        assertThat(resultado).isTrue();
        verify(politicaRecursoOutputPort).exigeSoloAutenticacion("GET", "/api/usuarios/mi/perfil");
    }
}
