package com.arquisoft.entregables.infrastructure.paqueteentregable.query.secondaryadapter.minio;

import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.EnlaceDescarga;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnlaceDescargaPaqueteMinioQueryOutputAdapterTest {

    @Mock
    private AlmacenamientoObjetos almacenamientoObjetos;

    @InjectMocks
    private EnlaceDescargaPaqueteMinioQueryOutputAdapter adapter;

    @Test
    void debeFirmarConBucketClaveYNombre() {
        // Arrange
        var expira = Instant.now().plusSeconds(300);
        when(almacenamientoObjetos.generarEnlaceDescarga(any()))
                .thenReturn(new EnlaceDescarga("https://minio/firmado", expira));

        // Act
        var enlace = adapter.generar("bucket", "paquetes/x.zip", "p.zip");

        // Assert
        var captor = ArgumentCaptor.forClass(SolicitudEnlaceDescarga.class);
        verify(almacenamientoObjetos).generarEnlaceDescarga(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new SolicitudEnlaceDescarga("bucket", "paquetes/x.zip", "p.zip"));
        assertThat(enlace.url()).isEqualTo("https://minio/firmado");
        assertThat(enlace.expiraEn()).isEqualTo(expira);
    }
}
