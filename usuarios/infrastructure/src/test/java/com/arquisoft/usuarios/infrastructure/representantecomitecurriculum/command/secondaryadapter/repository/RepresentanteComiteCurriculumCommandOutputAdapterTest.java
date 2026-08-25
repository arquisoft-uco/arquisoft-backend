package com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.entity.RepresentanteComiteCurriculumEntity;
import com.arquisoft.usuarios.infrastructure.representantecomitecurriculum.command.secondaryadapter.entity.RepresentanteComiteCurriculumJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepresentanteComiteCurriculumCommandOutputAdapterTest {

    @Mock
    private RepresentanteComiteCurriculumCommandRepository repository;

    @InjectMocks
    private RepresentanteComiteCurriculumCommandOutputAdapter adapter;

    @Test
    void debeAgregarRepresentante_cuandoEntityValida() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        var entity = new RepresentanteComiteCurriculumEntity(usuario);

        // Act
        adapter.agregarRepresentante(entity);

        // Assert
        verify(repository, times(1)).save(any(RepresentanteComiteCurriculumJpaEntity.class));
    }

    @Test
    void debeRetornarTrue_cuandoExistePorUsuario() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        when(repository.existsByUsuario(usuario)).thenReturn(true);

        // Act
        Boolean resultado = adapter.existePorUsuario(usuario);

        // Assert
        assertThat(resultado).isTrue();
        verify(repository, times(1)).existsByUsuario(usuario);
    }

    @Test
    void debeRetornarFalse_cuandoNoExistePorUsuario() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        when(repository.existsByUsuario(usuario)).thenReturn(false);

        // Act
        Boolean resultado = adapter.existePorUsuario(usuario);

        // Assert
        assertThat(resultado).isFalse();
        verify(repository, times(1)).existsByUsuario(usuario);
    }
}
