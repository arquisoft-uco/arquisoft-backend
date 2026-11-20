package com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EstudianteProyectoGradoKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.hibernate.exception.ConstraintViolationException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EstudianteProyectoGradoCommandOutputAdapterTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EstudianteProyectoGradoCommandRepository repository;

    private final AppLogger logger = mock(AppLogger.class);

    private EstudianteProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteProyectoGradoCommandOutputAdapter(repository, logger);
    }

    private EstudianteProyectoGradoEntity vinculo(UUID estudiante, UUID proyectoGrado, Instant eliminadoEn) {
        return new EstudianteProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), estudiante, proyectoGrado, "Ana", "ana@uco.edu.co", OCURRIDO_EN, eliminadoEn);
    }

    @Test
    void debeGuardarYRecuperarSoloLosVinculosDelProyecto_cuandoHayVariosProyectos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vigente = vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, UtilFecha.VACIO);
        var eliminado = vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, Instant.parse("2026-10-21T10:00:00Z"));
        var ajeno = vinculo(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilFecha.VACIO);

        // Act
        adapter.guardarTodos(List.of(vigente, eliminado, ajeno));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.consultarPorProyectoGrado(proyectoGrado)).containsExactlyInAnyOrder(vigente, eliminado);
        assertThat(entityManager.find(EstudianteProyectoGradoJpaEntity.class, vigente.id()).getEliminadoEn()).isNull();
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_GUARDADOS, 3);
    }

    @Test
    void debeBuscarPorElParEstudianteProyecto_cuandoExisteElVinculo() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var buscado = vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, UtilFecha.VACIO);
        var otroProyecto = vinculo(buscado.estudiante(), UtilUUID.generarNuevoUUID(), UtilFecha.VACIO);
        adapter.guardarTodos(List.of(buscado, otroProyecto));
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorEstudianteYProyectoGrado(buscado.estudiante(), proyectoGrado);
        var ausente = adapter.obtenerPorEstudianteYProyectoGrado(UtilUUID.generarNuevoUUID(), proyectoGrado);

        // Assert
        assertThat(encontrado).contains(buscado);
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeMarcarLaBajaLogica_cuandoSeGuardaElVinculoRemovido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vigente = vinculo(UtilUUID.generarNuevoUUID(), proyectoGrado, UtilFecha.VACIO);
        adapter.guardarTodos(List.of(vigente));
        entityManager.flush();
        entityManager.clear();
        var removidoEn = Instant.parse("2026-10-21T10:00:00Z");
        var removido = new EstudianteProyectoGradoEntity(
                vigente.id(), vigente.estudiante(), proyectoGrado, "Ana", "ana@uco.edu.co", removidoEn, removidoEn);

        // Act
        adapter.guardar(removido);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.consultarPorProyectoGrado(proyectoGrado)).containsExactly(removido);
        assertThat(entityManager.find(EstudianteProyectoGradoJpaEntity.class, vigente.id()).getEliminadoEn())
                .isEqualTo(removidoEn);
    }

    @Test
    void debeDevolverVacio_cuandoElProyectoNoTieneVinculos() {
        // Act
        var vinculos = adapter.consultarPorProyectoGrado(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(vinculos).isEmpty();
    }

    @Test
    void debeRechazarElParRepetido_cuandoSeGuardaElMismoEstudianteEnElMismoProyecto() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        adapter.guardarTodos(List.of(vinculo(estudiante, proyectoGrado, UtilFecha.VACIO)));
        entityManager.flush();
        var repetido = vinculo(estudiante, proyectoGrado, UtilFecha.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> {
            adapter.guardarTodos(List.of(repetido));
            entityManager.flush();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    private EstudianteProyectoGradoEntity conContacto(UUID proyectoGrado, String nombre, String email,
                                                      Instant eliminadoEn) {
        return new EstudianteProyectoGradoEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                proyectoGrado, nombre, email, OCURRIDO_EN, eliminadoEn);
    }

    @Test
    void debeGuardarYLeerElContacto_cuandoSeGuardaElVinculo() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vinculo = conContacto(proyectoGrado, "Ana Perez", "ana@uco.edu.co", UtilFecha.VACIO);

        // Act
        adapter.guardar(vinculo);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var jpa = entityManager.find(EstudianteProyectoGradoJpaEntity.class, vinculo.id());
        assertThat(jpa.getNombre()).isEqualTo("Ana Perez");
        assertThat(jpa.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(adapter.consultarPorProyectoGrado(proyectoGrado)).containsExactly(vinculo);
    }

    @Test
    void debeDevolverSoloVigentesConEmail_cuandoConsultaContactosPorProyecto() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vigente = conContacto(proyectoGrado, "Ana", "ana@uco.edu.co", UtilFecha.VACIO);
        var dadoDeBaja = conContacto(proyectoGrado, "Luis", "luis@uco.edu.co", Instant.parse("2026-10-21T10:00:00Z"));
        var sinEmail = conContacto(proyectoGrado, "Sin Correo", null, UtilFecha.VACIO);
        var emailEnBlanco = conContacto(proyectoGrado, "Blanco", "  ", UtilFecha.VACIO);
        var ajeno = conContacto(UtilUUID.generarNuevoUUID(), "Otro", "otro@uco.edu.co", UtilFecha.VACIO);
        adapter.guardarTodos(List.of(vigente, dadoDeBaja, sinEmail, emailEnBlanco, ajeno));
        entityManager.flush();
        entityManager.clear();

        // Act
        var contactos = adapter.consultarContactosVigentesPorProyectoGrado(proyectoGrado);

        // Assert
        assertThat(contactos).containsExactly(vigente);
    }

    @Test
    void debeDevolverSoloVigentesConEmailPorJoin_cuandoConsultaContactosPorEntregable() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        entityManager.persist(EntregableProyectoGradoJpaEntity.builder()
                .id(entregable).proyectoGrado(proyectoGrado).version(1).asesor(UtilUUID.generarNuevoUUID()).build());
        var vigente = conContacto(proyectoGrado, "Ana", "ana@uco.edu.co", UtilFecha.VACIO);
        var dadoDeBaja = conContacto(proyectoGrado, "Luis", "luis@uco.edu.co", Instant.parse("2026-10-21T10:00:00Z"));
        var sinEmail = conContacto(proyectoGrado, "Sin Correo", null, UtilFecha.VACIO);
        var ajeno = conContacto(UtilUUID.generarNuevoUUID(), "Otro", "otro@uco.edu.co", UtilFecha.VACIO);
        adapter.guardarTodos(List.of(vigente, dadoDeBaja, sinEmail, ajeno));
        entityManager.flush();
        entityManager.clear();

        // Act
        var contactos = adapter.consultarContactosVigentesPorEntregable(entregable);
        var deOtroEntregable = adapter.consultarContactosVigentesPorEntregable(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(contactos).containsExactly(vigente);
        assertThat(deOtroEntregable).isEmpty();
    }
}
