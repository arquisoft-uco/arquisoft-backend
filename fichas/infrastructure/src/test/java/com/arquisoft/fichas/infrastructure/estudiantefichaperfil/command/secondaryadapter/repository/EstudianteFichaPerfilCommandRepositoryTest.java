package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository.EstudianteCommandRepository;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstudianteFichaPerfilCommandRepositoryTest {

    @Autowired
    private EstudianteFichaPerfilCommandRepository repository;

    @Autowired
    private EstudianteCommandRepository estudianteRepository;

    @Test
    void debeRetornarFalse_cuandoRelacionNoExiste() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act
        boolean resultado = repository.existsByFichaPerfilIdAndEstudianteId(fichaId, estudianteId);

        // Assert
        assertThat(resultado).isFalse();
    }

    @Test
    void debeContarRelacionesDeLaFicha_cuandoExistenVarias() {
        // Arrange — la entidad usa columnas UUID crudas (sin @ManyToOne), no requiere filas padre
        UUID fichaId = UUID.randomUUID();
        UUID otraFicha = UUID.randomUUID();
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaId).estudianteId(UUID.randomUUID()).build());
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaId).estudianteId(UUID.randomUUID()).build());
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(otraFicha).estudianteId(UUID.randomUUID()).build());

        // Act
        long resultado = repository.countByFichaPerfilId(fichaId);

        // Assert
        assertThat(resultado).isEqualTo(2);
    }

    @Test
    void debeEliminar_cuandoRelacionExisteEnBD() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        var entity = EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID())
                .fichaPerfilId(fichaId)
                .estudianteId(estudianteId)
                .build();
        repository.saveAndFlush(entity);

        // Act
        repository.deleteByFichaPerfilIdAndEstudianteId(fichaId, estudianteId);
        repository.flush();

        // Assert
        boolean existe = repository.existsByFichaPerfilIdAndEstudianteId(fichaId, estudianteId);
        assertThat(existe).isFalse();
    }

    @Test
    void debeRetornarLosContactosDeLaFicha_cuandoExistenVarios() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID otraFicha = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        UUID estudianteDeOtraFicha = UUID.randomUUID();
        estudianteRepository.saveAndFlush(EstudianteJpaEntity.builder()
                .id(estudiante1).identificador("1000000001").nombre("Ana Gomez")
                .email("ana.gomez@soyuco.edu.co").build());
        estudianteRepository.saveAndFlush(EstudianteJpaEntity.builder()
                .id(estudiante2).identificador("1000000002").nombre("Luis Ruiz")
                .email("luis.ruiz@soyuco.edu.co").build());
        estudianteRepository.saveAndFlush(EstudianteJpaEntity.builder()
                .id(estudianteDeOtraFicha).identificador("1000000003").nombre("Otro Estudiante")
                .email("otro@soyuco.edu.co").build());
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaId).estudianteId(estudiante1).build());
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaId).estudianteId(estudiante2).build());
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(otraFicha).estudianteId(estudianteDeOtraFicha).build());

        // Act
        var resultado = repository.findContactosByFichaPerfilId(fichaId);

        // Assert
        assertThat(resultado)
                .extracting("nombre", "email")
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                        org.assertj.core.groups.Tuple.tuple("Luis Ruiz", "luis.ruiz@soyuco.edu.co"));
    }
}
