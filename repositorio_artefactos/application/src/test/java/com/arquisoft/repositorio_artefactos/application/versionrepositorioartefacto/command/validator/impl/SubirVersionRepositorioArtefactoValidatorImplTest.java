package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.impl;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoNoEncontradoException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaModificadaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaNoCorrespondeException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaPendienteNoEncontradaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.DocumentoNoExisteException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.DocumentoYaExisteException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubirVersionRepositorioArtefactoValidatorImplTest {

    private static final long MAXIMO = 5_242_880L;
    private static final String TIPO = "application/pdf";
    private static final String ETAG = "etag-1";

    private final SubirVersionRepositorioArtefactoValidatorImpl validator =
            new SubirVersionRepositorioArtefactoValidatorImpl();

    private SubidaVersionRepositorioArtefactoDomain subida;
    private AdministradorDomain administrador;
    private RepositorioArtefactoDomain repositorio;
    private CargaPendienteDomain carga;
    private AnalisisArchivoDomain analisis;

    @BeforeEach
    void setUp() {
        subida = SubidaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Formato A", false);
        administrador = AdministradorDomain.crear(
                subida.getSolicitante(), "123", "Ana", "ana@uco.edu.co", Instant.now());
        repositorio = RepositorioArtefactoDomain.reconstruir(
                subida.getRepositorio(), "Repo", "Desc", UtilUUID.generarNuevoUUID());
        carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "formato.pdf", TIPO, 2048L, ETAG);
        analisis = AnalisisArchivoDomain.reconstruir(TIPO, "h".repeat(64), ETAG);
    }

    private void validar(int ultimoNumero) {
        validator.validarCarga(subida, administrador, repositorio, carga, MAXIMO, ultimoNumero);
        validator.validarContenido(subida, carga, analisis);
    }

    @Test
    void debePasar_cuandoTodoValido() {
        // Act & Assert
        assertThatCode(() -> validar(2)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoAdministradorNoVigente() {
        // Arrange
        administrador = AdministradorDomain.VACIO;

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(AdministradorNoVigenteException.class);
    }

    @Test
    void debeLanzar_cuandoRepositorioNoExiste() {
        // Arrange
        repositorio = RepositorioArtefactoDomain.VACIO;

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(RepositorioArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoCargaNoExiste() {
        // Arrange
        carga = CargaPendienteDomain.VACIO;

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(CargaPendienteNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoCargaEsDeOtroSolicitante() {
        // Arrange
        carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                UtilUUID.generarNuevoUUID(), "formato.pdf", TIPO, 2048L, ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(CargaNoCorrespondeException.class);
    }

    @Test
    void debeLanzar_cuandoEtagsDifieren() {
        // Arrange
        analisis = AnalisisArchivoDomain.reconstruir(TIPO, "h".repeat(64), "otro-etag");

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(CargaModificadaException.class);
    }

    @Test
    void debeLanzar_cuandoElAnalisisEstaVacio() {
        // Arrange
        analisis = AnalisisArchivoDomain.VACIO;

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(CargaModificadaException.class);
    }

    @Test
    void debeLanzar_cuandoTamanoExcede() {
        // Arrange
        carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "formato.pdf", TIPO, MAXIMO + 1, ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(TamanoArchivoExcedidoException.class);
    }

    @Test
    void debeLanzar_cuandoTipoDetectadoNoCoincide() {
        // Arrange
        analisis = AnalisisArchivoDomain.reconstruir("application/zip", "h".repeat(64), ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(TipoArchivoNoCoincideException.class);
    }

    @Test
    void debeLanzar_cuandoEsNuevoYYaHayVersiones() {
        // Arrange
        subida = SubidaVersionRepositorioArtefactoDomain.crear(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "Formato A", true);

        // Act & Assert
        assertThatThrownBy(() -> validar(1)).isInstanceOf(DocumentoYaExisteException.class);
    }

    @Test
    void debeLanzar_cuandoNoEsNuevoYNoHayVersiones() {
        // Act & Assert
        assertThatThrownBy(() -> validar(0)).isInstanceOf(DocumentoNoExisteException.class);
    }

    @Test
    void debePasar_cuandoEsNuevoYNoHayVersiones() {
        // Arrange
        subida = SubidaVersionRepositorioArtefactoDomain.crear(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "Formato A", true);

        // Act & Assert
        assertThatCode(() -> validar(0)).doesNotThrowAnyException();
    }

    @Test
    void debePriorizar_administradorAntesQueRepositorio() {
        // Arrange
        administrador = AdministradorDomain.VACIO;
        repositorio = RepositorioArtefactoDomain.VACIO;

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(AdministradorNoVigenteException.class);
    }

    @Test
    void debePriorizar_cargaInexistenteAntesQueAjena() {
        // Arrange
        carga = CargaPendienteDomain.VACIO;
        analisis = AnalisisArchivoDomain.VACIO;

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(CargaPendienteNoEncontradaException.class);
    }

    @Test
    void debePriorizar_tamanoAntesQueTipo() {
        // Arrange
        carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "formato.pdf", TIPO, MAXIMO + 1, ETAG);
        analisis = AnalisisArchivoDomain.reconstruir("application/zip", "h".repeat(64), ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(TamanoArchivoExcedidoException.class);
    }

    @Test
    void debePriorizar_documentoAntesQueTipo() {
        // Arrange
        analisis = AnalisisArchivoDomain.reconstruir("application/zip", "h".repeat(64), ETAG);

        // Act & Assert
        assertThatThrownBy(() -> validar(0)).isInstanceOf(DocumentoNoExisteException.class);
    }

    @Test
    void debePriorizar_repositorioAntesQueCarga() {
        // Arrange
        repositorio = RepositorioArtefactoDomain.VACIO;
        carga = CargaPendienteDomain.VACIO;

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(RepositorioArtefactoNoEncontradoException.class);
    }

    @Test
    void debePriorizar_cargaAjenaAntesQueModificada() {
        // Arrange
        carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                UtilUUID.generarNuevoUUID(), "formato.pdf", TIPO, 2048L, ETAG);
        analisis = AnalisisArchivoDomain.reconstruir(TIPO, "h".repeat(64), "otro-etag");

        // Act & Assert
        assertThatThrownBy(() -> validar(2)).isInstanceOf(CargaNoCorrespondeException.class);
    }

    @Test
    void debePriorizar_tamanoAntesQueCargaModificada() {
        // Arrange
        carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "formato.pdf", TIPO, MAXIMO + 1, ETAG);
        analisis = AnalisisArchivoDomain.reconstruir("application/zip", "h".repeat(64), "otro-etag");

        // Act & Assert
        assertThatThrownBy(() -> validar(0)).isInstanceOf(TamanoArchivoExcedidoException.class);
    }
}
