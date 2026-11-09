package com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoParaPaqueteEntity;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.mapper.PaqueteEntregableJpaMapper;
import com.arquisoft.artefactos.infrastructure.paqueteentregable.command.secondaryadapter.projection.ArtefactoParaPaqueteProjection;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.PaqueteEntregableKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilNumero;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaqueteEntregableCommandOutputAdapter implements PaqueteEntregableOutputPort {

    private final PaqueteEntregableCommandRepository paqueteEntregableCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(PaqueteEntregableEntity paquete) {
        paqueteEntregableCommandRepository.save(PaqueteEntregableJpaMapper.toJpaEntity(paquete));
        logger.debug(PaqueteEntregableKey.LOG_GUARDADO, paquete.id());
    }

    @Override
    public boolean existePorEntregable(UUID entregableProyectoGrado) {
        return paqueteEntregableCommandRepository.existsByEntregableProyectoGrado(entregableProyectoGrado);
    }

    @Override
    public List<ArtefactoParaPaqueteEntity> obtenerArtefactosParaPaquete(UUID proyectoGrado, String estadoCerrada) {
        return paqueteEntregableCommandRepository.obtenerArtefactosParaPaquete(proyectoGrado, estadoCerrada).stream()
                .map(PaqueteEntregableCommandOutputAdapter::toEntity)
                .toList();
    }

    private static ArtefactoParaPaqueteEntity toEntity(ArtefactoParaPaqueteProjection fila) {
        return new ArtefactoParaPaqueteEntity(
                fila.getArtefacto(),
                fila.getNombreDocumento(),
                UtilObjeto.esNulo(fila.getVersionArtefacto())
                        ? UtilUUID.obtenerUUIDPorDefecto() : fila.getVersionArtefacto(),
                UtilNumero.obtenerPorDefecto(fila.getNumeroVersionCerrada()),
                UtilObjeto.esNulo(fila.getUriArchivo()) ? UtilTexto.VACIO : fila.getUriArchivo(),
                UtilObjeto.esNulo(fila.getFechaVersion()) ? UtilFecha.VACIO : fila.getFechaVersion(),
                UtilNumero.obtenerPorDefecto(fila.getUltimaVersion()));
    }
}
