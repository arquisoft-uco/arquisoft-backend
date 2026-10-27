package com.arquisoft.shared.tika;

import com.arquisoft.shared.tika.model.InspeccionArchivo;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TikaInspectorArchivo implements InspectorArchivo {

    private final Detector detector;

    public TikaInspectorArchivo() {
        this(new DefaultDetector());
    }

    TikaInspectorArchivo(Detector detector) {
        this.detector = detector;
    }

    @Override
    public InspeccionArchivo inspeccionar(InputStream contenido, long limiteBytes) throws IOException {
        var inspector = new InspectorFlujoArchivo(contenido, limiteBytes);
        String tipoDetectado;
        try (var flujo = TikaInputStream.get(inspector)) {
            tipoDetectado = detector.detect(flujo, new Metadata()).getBaseType().toString();
            inspector.drenar();
        }
        return new InspeccionArchivo(tipoDetectado, inspector.hashHexadecimal());
    }
}
