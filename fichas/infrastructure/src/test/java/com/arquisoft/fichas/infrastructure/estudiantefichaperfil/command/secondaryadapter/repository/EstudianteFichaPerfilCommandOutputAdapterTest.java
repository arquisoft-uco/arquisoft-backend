package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.mapper.EstudianteFichaPerfilMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteFichaPerfilCommandOutputAdapterTest {

    @Mock
    private EstudianteFichaPerfilCommandRepository repository;

    private EstudianteFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteFichaPerfilCommandOutputAdapter(repository);
    }

    @Test
    void debeGuardar_cuandoRelacionValida() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        EstudianteFichaPerfilDomain relacion = EstudianteFichaPerfilDomain.crear(
                fichaId, List.of(estudianteId)).get(0);
        EstudianteFichaPerfilEntity entity = EstudianteFichaPerfilMapper.toEntity(relacion);

        // Act
        adapter.vincularEstudiante(entity);

        // Assert — el adapter ya no traduce: guarda la entidad que le entrego el caso de uso
        verify(repository, times(1)).save(entity);
    }

    @Test
    void debeRetornarTrue_cuandoRelacionExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        when(repository.existsByFichaPerfilIdAndEstudianteId(fichaId, estudianteId)).thenReturn(true);

        // Act
        boolean resultado = adapter.existePorFichaYEstudiante(fichaId, estudianteId);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoRelacionNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        when(repository.existsByFichaPerfilIdAndEstudianteId(fichaId, estudianteId)).thenReturn(false);

        // Act
        boolean resultado = adapter.existePorFichaYEstudiante(fichaId, estudianteId);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    void debeRetornarConteo_cuandoContarPorFichaPerfilId() {
        // Arrange
        UUID fichaId = UUID.randomUUID();

        when(repository.countByFichaPerfilId(fichaId)).thenReturn(2L);

        // Act
        long resultado = adapter.contarPorFichaPerfilId(fichaId);

        // Assert
        assertThat(resultado).isEqualTo(2L);
    }

}
