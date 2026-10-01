package com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.EstudianteFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.ContactoEstudianteEntity;
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
class ContactosDeFichaFinderImplTest {

    @Mock
    private EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @InjectMocks
    private ContactosDeFichaFinderImpl finder;

    @Test
    void debeMapearLosContactos_cuandoElOutputPortLosRetorna() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        when(estudianteFichaPerfilOutputPort.obtenerContactosDeFicha(fichaPerfilId))
                .thenReturn(List.of(new ContactoEstudianteEntity("Ana Gomez", "ana.gomez@soyuco.edu.co")));

        // Act
        var resultado = finder.obtener(fichaPerfilId);

        // Assert
        assertThat(resultado)
                .extracting("nombre", "email")
                .containsExactly(org.assertj.core.groups.Tuple.tuple("Ana Gomez", "ana.gomez@soyuco.edu.co"));
    }

    @Test
    void debeRetornarListaVacia_cuandoLaFichaNoTieneEstudiantes() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        when(estudianteFichaPerfilOutputPort.obtenerContactosDeFicha(fichaPerfilId)).thenReturn(List.of());

        // Act
        var resultado = finder.obtener(fichaPerfilId);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
