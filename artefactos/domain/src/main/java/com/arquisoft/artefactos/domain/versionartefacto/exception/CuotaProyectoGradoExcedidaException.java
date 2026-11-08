package com.arquisoft.artefactos.domain.versionartefacto.exception;

import com.arquisoft.artefactos.domain.versionartefacto.model.CuotaProyectoGrado;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.util.UtilNumero;

public final class CuotaProyectoGradoExcedidaException extends DomainException {

    public CuotaProyectoGradoExcedidaException(CuotaProyectoGrado cuota) {
        super(BaseError.conDetalle(
                ArtefactosCodes.VersionArtefacto.CUOTA_PROYECTO_GRADO_EXCEDIDA,
                Mensajes.formatear(VersionArtefactoKey.ERROR_CUOTA_PROYECTO_EXCEDIDA,
                        cuota.nombreObjeto(), cuota.nombreProyecto(),
                        UtilNumero.formatearTamano(Math.max(0L, cuota.cuotaBytes() - cuota.usoBytes())),
                        UtilNumero.formatearTamano(cuota.cuotaBytes())),
                Mensajes.formatear(VersionArtefactoKey.ERROR_CUOTA_PROYECTO_EXCEDIDA_DETALLE,
                        cuota.proyectoGrado(), cuota.usoBytes(), cuota.cuotaBytes(), cuota.tamanoNuevoBytes())
        ));
    }
}
