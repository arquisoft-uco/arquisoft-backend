package com.arquisoft.biblioteca.application.documentacion.command.validator.impl;

import com.arquisoft.biblioteca.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.biblioteca.domain.documentacion.RegistroDocumentacionDomain;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;
import com.arquisoft.shared.exception.BaseException;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarDocumentacionValidatorImplTest {

    private final RegistrarDocumentacionValidatorImpl validator = new RegistrarDocumentacionValidatorImpl();

    private final UUID entregableId = UtilUUID.generarNuevoUUID();
    private final UUID bibliotecarioId = UtilUUID.generarNuevoUUID();
    private final RegistroDocumentacionDomain registro = RegistroDocumentacionDomain.crear(entregableId, bibliotecarioId);
    private final EntregableProyectoGradoDomain entregableConArchivo = EntregableProyectoGradoDomain.reconstruir(
            entregableId, UtilUUID.generarNuevoUUID(), 1, EstadoEntregable.APROBADO, "a/b.zip", "b.zip");
    private final EntregableProyectoGradoDomain entregableSinArchivo = EntregableProyectoGradoDomain.reconstruir(
            entregableId, UtilUUID.generarNuevoUUID(), 1, EstadoEntregable.APROBADO, null, null);
    private final BibliotecarioDomain bibliotecarioVigente = BibliotecarioDomain.reconstruir(
            bibliotecarioId, "123", "Ana", "ana@uco.edu.co", Instant.parse("2026-11-15T10:00:00Z"), UtilFecha.VACIO);
    private final List<AutorDeEntregable> autoresVigentes = List.of(
            new AutorDeEntregable(UtilUUID.generarNuevoUUID(), true));

    @Test
    void debePasar_cuandoTodasLasReglasSeCumplen() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                registro, entregableConArchivo, false, bibliotecarioVigente, autoresVigentes))
                .doesNotThrowAnyException();
    }

    @Test
    void debeGanarEntregableNoEncontrado_cuandoTodoLoDemasTambienFalla() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                registro, EntregableProyectoGradoDomain.VACIO, true, BibliotecarioDomain.VACIO, List.of()))
                .isInstanceOfSatisfying(BaseException.class, ex -> assertThat(ex.getCodigoError())
                        .isEqualTo(BibliotecaCodes.EntregableProyectoGrado.NO_ENCONTRADO));
    }

    @Test
    void debeGanarDocumentacionYaRegistrada_cuandoTambienFaltaElArchivo() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                registro, entregableSinArchivo, true, BibliotecarioDomain.VACIO, List.of()))
                .isInstanceOfSatisfying(BaseException.class, ex -> assertThat(ex.getCodigoError())
                        .isEqualTo(BibliotecaCodes.Documentacion.YA_REGISTRADA));
    }

    @Test
    void debeGanarArchivoNoDisponible_cuandoTambienElBibliotecarioNoEstaVigente() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                registro, entregableSinArchivo, false, BibliotecarioDomain.VACIO, List.of()))
                .isInstanceOfSatisfying(BaseException.class, ex -> assertThat(ex.getCodigoError())
                        .isEqualTo(BibliotecaCodes.EntregableProyectoGrado.ARCHIVO_NO_DISPONIBLE));
    }

    @Test
    void debeGanarBibliotecarioNoVigente_cuandoTambienNoHayAutores() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                registro, entregableConArchivo, false, BibliotecarioDomain.VACIO, List.of()))
                .isInstanceOfSatisfying(BaseException.class, ex -> assertThat(ex.getCodigoError())
                        .isEqualTo(BibliotecaCodes.Bibliotecario.NO_VIGENTE));
    }

    @Test
    void debeRechazarSinAutoresYAutorNoVigente_cuandoSoloFallanLasReglasDeAutores() {
        // Arrange
        var autorAusente = List.of(new AutorDeEntregable(UtilUUID.generarNuevoUUID(), false));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                registro, entregableConArchivo, false, bibliotecarioVigente, List.of()))
                .isInstanceOfSatisfying(BaseException.class, ex -> assertThat(ex.getCodigoError())
                        .isEqualTo(BibliotecaCodes.EstudianteProyectoGrado.SIN_AUTORES));
        assertThatThrownBy(() -> validator.validar(
                registro, entregableConArchivo, false, bibliotecarioVigente, autorAusente))
                .isInstanceOfSatisfying(BaseException.class, ex -> assertThat(ex.getCodigoError())
                        .isEqualTo(BibliotecaCodes.EstudianteProyectoGrado.AUTOR_NO_VIGENTE));
    }
}
