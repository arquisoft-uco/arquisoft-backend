package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.entity.EstudianteFichaPerfilEntity;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.secondaryport.mapper.EstudianteFichaPerfilMapper;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
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
        adapter = new EstudianteFichaPerfilCommandOutputAdapter(repository, mock(AppLogger.class));
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

        // Assert — el adapter mapea a JpaEntity antes de guardar
        verify(repository, times(1)).save(argThat(jpaEntity ->
                jpaEntity.getId().equals(entity.id())
                        && jpaEntity.getFichaPerfilId().equals(entity.fichaPerfilId())
                        && jpaEntity.getEstudianteId().equals(entity.estudianteId())));
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

    @Test
    void debeRetornarLosEstudiantes_cuandoObtenerEstudiantesDeFicha() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        List<UUID> estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(repository.findEstudianteIdByFichaPerfilId(fichaId)).thenReturn(estudiantes);

        // Act
        List<UUID> resultado = adapter.obtenerEstudiantesDeFicha(fichaId);

        // Assert
        assertThat(resultado).isEqualTo(estudiantes);
    }

}
