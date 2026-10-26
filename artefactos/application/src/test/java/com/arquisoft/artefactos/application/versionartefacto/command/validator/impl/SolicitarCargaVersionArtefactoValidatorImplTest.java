package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.EstudianteNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitarCargaVersionArtefactoValidatorImplTest {

    private static final long MAXIMO = 5_242_880L;

    private final SolicitarCargaVersionArtefactoValidatorImpl validator =
            new SolicitarCargaVersionArtefactoValidatorImpl();

    private SolicitudCargaVersionArtefactoDomain solicitud;
    private ArtefactoDomain artefacto;

    @BeforeEach
    void setUp() {
        solicitud = SolicitudCargaVersionArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "informe.pdf", "application/pdf", 2048L);
        artefacto = ArtefactoDomain.reconstruir(solicitud.getArtefacto(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
    }

    @Test
    void debePasar_cuandoTodoValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(solicitud, artefacto, true, MAXIMO)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_primero() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, ArtefactoDomain.VACIO, false, 1L))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoPropietario_antesQueElTamano() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, artefacto, false, 1L))
                .isInstanceOf(EstudianteNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarTamanoExcedido() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(solicitud, artefacto, true, 1024L))
                .isInstanceOf(TamanoArchivoExcedidoException.class);
    }
}
