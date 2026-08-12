package com.arquisoft.fichas.application.fichaperfil.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
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
    void debeConvertirLaEntidadADominio_cuandoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorId = UUID.randomUUID();
        var entity = FichaPerfilEntity.builder()
                .id(fichaId)
                .tituloProyecto("Titulo de prueba")
                .asesorFicha(AsesorFichaEntity.builder().id(asesorId).build())
                .build();
        when(fichaPerfilOutputPort.buscarPorId(fichaId)).thenReturn(Optional.of(entity));

        // Act
        Optional<FichaPerfilDomain> resultado = finder.obtener(fichaId);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(fichaId);
        assertThat(resultado.get().getTituloProyecto()).isEqualTo("Titulo de prueba");
        assertThat(resultado.get().getAsesorFicha()).isEqualTo(asesorId);
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
