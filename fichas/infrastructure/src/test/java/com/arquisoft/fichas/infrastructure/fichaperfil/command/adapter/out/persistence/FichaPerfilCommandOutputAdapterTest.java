package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FichaPerfilCommandOutputAdapterTest {

    @Mock
    private FichaPerfilJpaRepository fichaPerfilJpaRepository;

    @Mock
    private AsesorFichaJpaRepository asesorFichaJpaRepository;

    private FichaPerfilCommandOutputAdapter adapter;

    private UUID fichaId;
    private UUID asesorId;

    @BeforeEach
    void setUp() {
        fichaId = UUID.randomUUID();
        asesorId = UUID.randomUUID();
        adapter = new FichaPerfilCommandOutputAdapter(fichaPerfilJpaRepository, asesorFichaJpaRepository,
                org.mockito.Mockito.mock(
                        com.arquisoft.fichas.infrastructure.estudiantefichaperfil.persistence.EstudianteFichaPerfilJpaRepository.class),
                org.mockito.Mockito.mock(com.arquisoft.shared.logger.AppLogger.class));
    }

    @Test
    void debeGuardarFichaPerfil_cuandoAgreggateEsValido() {
        // Arrange
        FichaPerfilAggregate aggregate = FichaPerfilAggregate.reconstruir(
                fichaId,
                "Proyecto de Prueba",
                asesorId
        );

        AsesorFichaJpaEntity asesorRef = new AsesorFichaJpaEntity();
        asesorRef.setId(asesorId);

        when(asesorFichaJpaRepository.getReferenceById(asesorId)).thenReturn(asesorRef);
        when(fichaPerfilJpaRepository.save(any(FichaPerfilJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        adapter.guardar(aggregate);

        // Assert
        verify(asesorFichaJpaRepository, times(1)).getReferenceById(asesorId);
        verify(fichaPerfilJpaRepository, times(1)).save(any(FichaPerfilJpaEntity.class));
    }

    @Test
    void debeBuscarPorId_cuandoIdExiste() {
        // Arrange
        AsesorFichaJpaEntity asesor = new AsesorFichaJpaEntity();
        asesor.setId(asesorId);

        FichaPerfilJpaEntity entity = new FichaPerfilJpaEntity();
        entity.setId(fichaId);
        entity.setTituloProyecto("Proyecto Test");
        entity.setAsesorFicha(asesor);

        when(fichaPerfilJpaRepository.findById(fichaId)).thenReturn(Optional.of(entity));

        // Act
        Optional<FichaPerfilAggregate> resultado = adapter.buscarPorId(fichaId);

        // Assert
        assertThat(resultado).isPresent();
        verify(fichaPerfilJpaRepository, times(1)).findById(fichaId);
    }

    @Test
    void debeRetornarVacio_cuandoIdNoExiste() {
        // Arrange
        when(fichaPerfilJpaRepository.findById(fichaId)).thenReturn(Optional.empty());

        // Act
        Optional<FichaPerfilAggregate> resultado = adapter.buscarPorId(fichaId);

        // Assert
        assertThat(resultado).isEmpty();
        verify(fichaPerfilJpaRepository, times(1)).findById(fichaId);
    }

    @Test
    void debeRetornarTrue_cuandoExistePorTitulo() {
        // Arrange
        String titulo = "Proyecto Unico";
        when(fichaPerfilJpaRepository.existsByTituloProyecto(titulo)).thenReturn(true);

        // Act
        boolean existe = adapter.existePorTituloProyecto(titulo);

        // Assert
        assertThat(existe).isTrue();
        verify(fichaPerfilJpaRepository, times(1)).existsByTituloProyecto(titulo);
    }

    @Test
    void debeRetornarFalse_cuandoNoExistePorTitulo() {
        // Arrange
        String titulo = "Proyecto Nuevo";
        when(fichaPerfilJpaRepository.existsByTituloProyecto(titulo)).thenReturn(false);

        // Act
        boolean existe = adapter.existePorTituloProyecto(titulo);

        // Assert
        assertThat(existe).isFalse();
        verify(fichaPerfilJpaRepository, times(1)).existsByTituloProyecto(titulo);
    }
}
