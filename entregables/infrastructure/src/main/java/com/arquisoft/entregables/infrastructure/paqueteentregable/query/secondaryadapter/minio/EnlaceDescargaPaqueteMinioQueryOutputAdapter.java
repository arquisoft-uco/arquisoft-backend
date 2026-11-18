package com.arquisoft.entregables.infrastructure.paqueteentregable.query.secondaryadapter.minio;

import com.arquisoft.entregables.application.paqueteentregable.query.readmodel.EnlaceDescargaPaqueteEntregableReadModel;
import com.arquisoft.entregables.application.paqueteentregable.query.secondaryport.EnlaceDescargaPaqueteQueryOutputPort;
import com.arquisoft.shared.minio.AlmacenamientoObjetos;
import com.arquisoft.shared.minio.model.SolicitudEnlaceDescarga;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnlaceDescargaPaqueteMinioQueryOutputAdapter implements EnlaceDescargaPaqueteQueryOutputPort {

    private final AlmacenamientoObjetos almacenamientoObjetos;

    @Override
    public EnlaceDescargaPaqueteEntregableReadModel generar(String bucket, String claveObjeto, String nombreArchivo) {
        var enlace = almacenamientoObjetos.generarEnlaceDescarga(
                new SolicitudEnlaceDescarga(bucket, claveObjeto, nombreArchivo));
        return new EnlaceDescargaPaqueteEntregableReadModel(enlace.url(), enlace.expiraEn());
    }
}
