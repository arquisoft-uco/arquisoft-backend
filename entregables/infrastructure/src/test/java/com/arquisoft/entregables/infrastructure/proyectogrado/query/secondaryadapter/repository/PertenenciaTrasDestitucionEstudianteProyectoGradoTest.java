package com.arquisoft.entregables.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.finder.impl.VinculoEstudianteProyectoGradoFinderImpl;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.finder.impl.VinculosDeProyectoGradoFinderImpl;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.usecase.impl.AgregarEstudiantesProyectoGradoUseCaseImpl;
import com.arquisoft.entregables.application.estudianteproyectogrado.command.usecase.impl.RemoverEstudianteProyectoGradoUseCaseImpl;
import com.arquisoft.entregables.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository.EstudianteProyectoGradoCommandOutputAdapter;
import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository.EstudianteProyectoGradoCommandRepository;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class PertenenciaTrasDestitucionEstudianteProyectoGradoTest {

    private static final Instant ASIGNADO_EN = Instant.parse("2026-10-20T10:00:00Z");
    private static final Instant DESTITUIDO_EN = Instant.parse("2026-10-21T10:00:00Z");

    @Autowired
    private EstudianteProyectoGradoCommandRepository commandRepository;

    @Autowired
    private EstudianteProyectoGradoAccesoQueryRepository queryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private AgregarEstudiantesProyectoGradoUseCaseImpl agregar;
    private RemoverEstudianteProyectoGradoUseCaseImpl remover;
    private EstudianteProyectoGradoAccesoQueryOutputAdapter consulta;

    @BeforeEach
    void setUp() {
        var logger = mock(AppLogger.class);
        var outputPort = new EstudianteProyectoGradoCommandOutputAdapter(commandRepository, logger);
        agregar = new AgregarEstudiantesProyectoGradoUseCaseImpl(
                outputPort, new VinculosDeProyectoGradoFinderImpl(outputPort), logger);
        remover = new RemoverEstudianteProyectoGradoUseCaseImpl(
                outputPort, new VinculoEstudianteProyectoGradoFinderImpl(outputPort), logger);
        consulta = new EstudianteProyectoGradoAccesoQueryOutputAdapter(queryRepository);
    }

    private void asignar(UUID estudiante, UUID proyectoGrado, Instant ocurridoEn) {
        var vinculo = EstudianteProyectoGradoDomain.crear(estudiante, proyectoGrado, ocurridoEn);
        agregar.ejecutar(AgregacionEstudiantesProyectoGradoDomain.crear(proyectoGrado, List.of(vinculo)));
        entityManager.flush();
        entityManager.clear();
    }

    private void destituir(UUID estudiante, UUID proyectoGrado, Instant ocurridoEn) {
        remover.ejecutar(EstudianteProyectoGradoDomain.crear(estudiante, proyectoGrado, ocurridoEn));
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeDejarDePertenecer_cuandoSeProcesaLaDestitucion() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        asignar(estudiante, proyectoGrado, ASIGNADO_EN);
        var perteneciaAntes = consulta.perteneceAlProyecto(estudiante, proyectoGrado);

        // Act
        destituir(estudiante, proyectoGrado, DESTITUIDO_EN);

        // Assert
        assertThat(perteneciaAntes).isTrue();
        assertThat(consulta.perteneceAlProyecto(estudiante, proyectoGrado)).isFalse();
    }

    @Test
    void debeSeguirSinPertenecer_cuandoLlegaUnaAsignacionMasVieja() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        asignar(estudiante, proyectoGrado, ASIGNADO_EN);
        destituir(estudiante, proyectoGrado, DESTITUIDO_EN);

        // Act
        asignar(estudiante, proyectoGrado, ASIGNADO_EN.minusSeconds(60));

        // Assert
        assertThat(consulta.perteneceAlProyecto(estudiante, proyectoGrado)).isFalse();
    }

    @Test
    void debeSeguirSinPertenecer_cuandoLaDestitucionLlegaAntesQueLaAsignacion() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        destituir(estudiante, proyectoGrado, DESTITUIDO_EN);

        // Act
        asignar(estudiante, proyectoGrado, ASIGNADO_EN);

        // Assert
        assertThat(consulta.perteneceAlProyecto(estudiante, proyectoGrado)).isFalse();
    }

    @Test
    void debePertenecerDeNuevo_cuandoLlegaUnaAsignacionPosterior() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        asignar(estudiante, proyectoGrado, ASIGNADO_EN);
        destituir(estudiante, proyectoGrado, DESTITUIDO_EN);

        // Act
        asignar(estudiante, proyectoGrado, DESTITUIDO_EN.plusSeconds(60));

        // Assert
        assertThat(consulta.perteneceAlProyecto(estudiante, proyectoGrado)).isTrue();
    }
}
