package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.IntegranteFichaEntity;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.IntegranteFicha;
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
class IntegrantesVigentesDeFichaFinderImplTest {

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @InjectMocks
    private IntegrantesVigentesDeFichaFinderImpl finder;

    @Test
    void debeMapearCadaFilaAIntegranteConSuContacto_cuandoLaFichaTieneEstudiantesVigentes() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var ana = UUID.randomUUID();
        var luis = UUID.randomUUID();
        when(estudianteFichaPerfilOutputPort.obtenerIntegrantesVigentesDeFicha(fichaPerfil)).thenReturn(List.of(
                new IntegranteFichaEntity(ana, "Ana Gomez", "ana.gomez@soyuco.edu.co"),
                new IntegranteFichaEntity(luis, "Luis Diaz", "luis.diaz@soyuco.edu.co")));

        // Act
        var integrantes = finder.obtener(fichaPerfil);

        // Assert
        assertThat(integrantes).containsExactly(
                new IntegranteFicha(ana, new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co")),
                new IntegranteFicha(luis, new ContactoEstudiante("Luis Diaz", "luis.diaz@soyuco.edu.co")));
    }

    @Test
    void debeDevolverListaVacia_cuandoLaFichaNoTieneEstudiantesVigentes() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        when(estudianteFichaPerfilOutputPort.obtenerIntegrantesVigentesDeFicha(fichaPerfil)).thenReturn(List.of());

        // Act
        var integrantes = finder.obtener(fichaPerfil);

        // Assert
        assertThat(integrantes).isEmpty();
    }
}
