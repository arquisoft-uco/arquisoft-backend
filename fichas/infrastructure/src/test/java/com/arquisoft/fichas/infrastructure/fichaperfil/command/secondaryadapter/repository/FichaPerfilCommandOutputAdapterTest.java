package com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.entity.FichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.secondaryadapter.entity.FichaPerfilJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaPerfilCommandOutputAdapterTest {

    @Mock
    private FichaPerfilCommandRepository fichaPerfilRepository;

    private FichaPerfilCommandOutputAdapter adapter;

    private UUID fichaId;
    private UUID asesorId;

    @BeforeEach
    void setUp() {
        fichaId = UUID.randomUUID();
        asesorId = UUID.randomUUID();
        adapter = new FichaPerfilCommandOutputAdapter(
                fichaPerfilRepository,
                mock(AppLogger.class),
                CatalogoMensajesResourceBundle.porDefecto());
    }

    @Test
    void debeMapearYGuardarLaEntidadComoJpaEntity_cuandoRegistraLaFicha() {
        // Arrange
        FichaPerfilEntity entity = fichaEntity();

        // Act
        adapter.registrarFicha(entity);

        // Assert
        verify(fichaPerfilRepository, times(1)).save(argThat(jpaEntity ->
                jpaEntity.getId().equals(fichaId)
                        && jpaEntity.getTituloProyecto().equals("Proyecto de Prueba")
                        && jpaEntity.getAsesorFicha().getId().equals(asesorId)));
    }

    @Test
    void debeDevolverLaEntidadMapeada_cuandoIdExiste() {
        // Arrange
        when(fichaPerfilRepository.findById(fichaId)).thenReturn(Optional.of(fichaJpaEntity()));

        // Act
        Optional<FichaPerfilEntity> resultado = adapter.buscarPorId(fichaId);

        // Assert
        assertThat(resultado).contains(fichaEntity());
        verify(fichaPerfilRepository, times(1)).findById(fichaId);
    }

    @Test
    void debeRetornarVacio_cuandoIdNoExiste() {
        // Arrange
        when(fichaPerfilRepository.findById(fichaId)).thenReturn(Optional.empty());

        // Act
        Optional<FichaPerfilEntity> resultado = adapter.buscarPorId(fichaId);

        // Assert
        assertThat(resultado).isEmpty();
        verify(fichaPerfilRepository, times(1)).findById(fichaId);
    }

    @Test
    void debeConstruirLaReferenciaDelAsesor_cuandoActualizaElAsesor() {
        // Act
        adapter.actualizarAsesor(fichaId, asesorId);

        // Assert
        verify(fichaPerfilRepository, times(1)).actualizarAsesorFicha(
                eq(fichaId), argThat(referencia -> referencia.getId().equals(asesorId)));
    }

    @Test
    void debeRetornarTrue_cuandoExistePorTitulo() {
        // Arrange
        String titulo = "Proyecto Unico";
        when(fichaPerfilRepository.existsByTituloProyecto(titulo)).thenReturn(true);

        // Act & Assert
        assertThat(adapter.existePorTituloProyecto(titulo)).isTrue();
        verify(fichaPerfilRepository, times(1)).existsByTituloProyecto(titulo);
    }

    @Test
    void debeRetornarFalse_cuandoNoExistePorTitulo() {
        // Arrange
        String titulo = "Proyecto Nuevo";
        when(fichaPerfilRepository.existsByTituloProyecto(titulo)).thenReturn(false);

        // Act & Assert
        assertThat(adapter.existePorTituloProyecto(titulo)).isFalse();
        verify(fichaPerfilRepository, times(1)).existsByTituloProyecto(titulo);
    }

    private FichaPerfilEntity fichaEntity() {
        return new FichaPerfilEntity(fichaId, "Proyecto de Prueba", asesorId);
    }

    private FichaPerfilJpaEntity fichaJpaEntity() {
        return FichaPerfilJpaEntity.builder()
                .id(fichaId)
                .tituloProyecto("Proyecto de Prueba")
                .asesorFicha(AsesorFichaJpaEntity.builder().id(asesorId).build())
                .build();
    }
}
