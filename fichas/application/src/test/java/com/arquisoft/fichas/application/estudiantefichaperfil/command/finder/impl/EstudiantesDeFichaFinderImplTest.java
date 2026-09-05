package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesDeFichaFinderImplTest {

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @InjectMocks
    private EstudiantesDeFichaFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_cuandoSeConsultanLosEstudiantesDeUnaFicha() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        List<UUID> estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(estudianteFichaPerfilOutputPort.obtenerEstudiantesDeFicha(fichaPerfilId))
                .thenReturn(estudiantes);

        // Act
        List<UUID> resultado = finder.obtener(fichaPerfilId);

        // Assert
        assertThat(resultado).isEqualTo(estudiantes);
    }
}
