package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoCongeladoException;
import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CuotaProyectoGradoExcedidaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.AlmacenamientoProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarVersionArtefactoCuotaValidatorImplTest {

    private static final long CUOTA = 1_073_741_824L;

    private final GenerarVersionArtefactoCuotaValidatorImpl validator = new GenerarVersionArtefactoCuotaValidatorImpl();

    private ArtefactoDomain artefacto;
    private CargaPendienteDomain carga;
    private ProyectoGradoDomain proyecto;

    @BeforeEach
    void setUp() {
        artefacto = ArtefactoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), null);
        carga = CargaPendienteDomain.reconstruir(UtilUUID.generarNuevoUUID(), artefacto.getId(),
                UtilUUID.generarNuevoUUID(), "informe.pdf", "application/pdf", 1000L, "etag");
        proyecto = ProyectoGradoDomain.reconstruir(artefacto.getProyectoGrado(), "Proyecto", null, null);
    }

    @Test
    void debePasar_cuandoSumaIgualCuota() {
        // Arrange
        var almacenamiento = new AlmacenamientoProyectoGrado(proyecto, CUOTA, CUOTA - 1000L);

        // Act & Assert
        assertThatCode(() -> validator.validar(artefacto, carga, almacenamiento)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoCuotaExcedida() {
        // Arrange
        var almacenamiento = new AlmacenamientoProyectoGrado(proyecto, CUOTA, CUOTA - 999L);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(artefacto, carga, almacenamiento))
                .isInstanceOf(CuotaProyectoGradoExcedidaException.class);
    }

    @Test
    void debeValidarExistenciaAntesQueCuota_cuandoProyectoNoExisteYElUsoExcede() {
        // Arrange
        var almacenamiento = new AlmacenamientoProyectoGrado(ProyectoGradoDomain.VACIO, CUOTA, CUOTA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(artefacto, carga, almacenamiento))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarCongelado_antesQueCuota() {
        // Arrange
        var congelado = ProyectoGradoDomain.reconstruir(artefacto.getProyectoGrado(), "Proyecto", null, Instant.now());
        var almacenamiento = new AlmacenamientoProyectoGrado(congelado, CUOTA, CUOTA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(artefacto, carga, almacenamiento))
                .isInstanceOf(ProyectoGradoCongeladoException.class);
    }
}
