package com.arquisoft.fichas.application.fichaperfil.command.finder.impl;

import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaPerfilFinderImplTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @InjectMocks
    private FichaPerfilFinderImpl finder;

    @Test
    void debeDevolverLaFicha_cuandoExiste() {
        // Arrange
        var ficha = FichaPerfilDomain.crear("Titulo de prueba", UUID.randomUUID());
        when(fichaPerfilOutputPort.buscarPorId(ficha.getId())).thenReturn(Optional.of(ficha));

        // Act
        Optional<FichaPerfilDomain> resultado = finder.obtener(ficha.getId());

        // Assert
        assertThat(resultado).contains(ficha);
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.empty());

        // Act
        Optional<FichaPerfilDomain> resultado = finder.obtener(fichaId);

        // Assert — el finder nunca lanza por "no encontrado"; eso lo decide la rule
        assertThat(resultado).isEmpty();
    }
}
