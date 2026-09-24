package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository.EstudianteCommandRepository;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.secondaryadapter.entity.EstudianteFichaPerfilJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
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
        // Arrange
        var fichaId = UUID.randomUUID();
        var otraFicha = UUID.randomUUID();
        vincular(fichaId, persistirEstudiante("1000000011", null));
        vincular(fichaId, persistirEstudiante("1000000012", null));
        vincular(otraFicha, persistirEstudiante("1000000013", null));

        // Act
        var resultado = repository.countVigentesByFichaPerfilId(fichaId);

        // Assert
        assertThat(resultado).isEqualTo(2);
    }

    @Test
    void debeExcluirDelConteo_cuandoElEstudianteEstaDadoDeBaja() {
        // Arrange
        var fichaId = UUID.randomUUID();
        vincular(fichaId, persistirEstudiante("1000000021", null));
        vincular(fichaId, persistirEstudiante("1000000022", Instant.now()));

        // Act
        var resultado = repository.countVigentesByFichaPerfilId(fichaId);

        // Assert
        assertThat(resultado).isEqualTo(1);
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
                .email("ana.gomez@soyuco.edu.co").ocurridoEn(Instant.now()).build());
        estudianteRepository.saveAndFlush(EstudianteJpaEntity.builder()
                .id(estudiante2).identificador("1000000002").nombre("Luis Ruiz")
                .email("luis.ruiz@soyuco.edu.co").ocurridoEn(Instant.now()).build());
        estudianteRepository.saveAndFlush(EstudianteJpaEntity.builder()
                .id(estudianteDeOtraFicha).identificador("1000000003").nombre("Otro Estudiante")
                .email("otro@soyuco.edu.co").ocurridoEn(Instant.now()).build());
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

    @Test
    void debeExcluirDeLosContactos_cuandoElEstudianteEstaDadoDeBaja() {
        // Arrange
        var fichaId = UUID.randomUUID();
        var vigente = persistirEstudiante("1000000031", null);
        vincular(fichaId, vigente);
        vincular(fichaId, persistirEstudiante("1000000032", Instant.now()));

        // Act
        var resultado = repository.findContactosByFichaPerfilId(fichaId);

        // Assert
        assertThat(resultado)
                .extracting("email")
                .containsExactly("1000000031@soyuco.edu.co");
    }

    private UUID persistirEstudiante(String identificador, Instant eliminadoEn) {
        var estudiante = EstudianteJpaEntity.builder()
                .id(UUID.randomUUID()).identificador(identificador).nombre("Estudiante " + identificador)
                .email(identificador + "@soyuco.edu.co").ocurridoEn(Instant.now()).eliminadoEn(eliminadoEn)
                .build();
        estudianteRepository.saveAndFlush(estudiante);
        return estudiante.getId();
    }

    private void vincular(UUID fichaPerfilId, UUID estudianteId) {
        repository.saveAndFlush(EstudianteFichaPerfilJpaEntity.builder()
                .id(UUID.randomUUID()).fichaPerfilId(fichaPerfilId).estudianteId(estudianteId).build());
    }
}
