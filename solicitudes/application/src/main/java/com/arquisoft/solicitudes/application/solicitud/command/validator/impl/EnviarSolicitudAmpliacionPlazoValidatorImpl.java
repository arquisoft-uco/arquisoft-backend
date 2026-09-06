package com.arquisoft.solicitudes.application.solicitud.command.validator.impl;

import com.arquisoft.solicitudes.application.solicitud.command.validator.EnviarSolicitudAmpliacionPlazoValidator;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudAmpliacionPlazoDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaAsignacionResponsable;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.solicitud.rules.DestinatarioAsignadoRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.DestinatarioExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.RemitenteExisteRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudUnicaRule;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.DestinatarioAsignadoRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.DestinatarioExisteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.RemitenteExisteRuleImpl;
import com.arquisoft.solicitudes.domain.solicitud.rules.impl.SolicitudUnicaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class EnviarSolicitudAmpliacionPlazoValidatorImpl
        implements EnviarSolicitudAmpliacionPlazoValidator {

    private final RemitenteExisteRule remitenteExisteRule;
    private final DestinatarioExisteRule destinatarioExisteRule;
    private final DestinatarioAsignadoRule destinatarioAsignadoRule;
    private final SolicitudUnicaRule solicitudUnicaRule;

    public EnviarSolicitudAmpliacionPlazoValidatorImpl() {
        this.remitenteExisteRule = new RemitenteExisteRuleImpl();
        this.destinatarioExisteRule = new DestinatarioExisteRuleImpl();
        this.destinatarioAsignadoRule = new DestinatarioAsignadoRuleImpl();
        this.solicitudUnicaRule = new SolicitudUnicaRuleImpl();
    }

    @Override
    public void validarExistenciaUsuarios(EnvioSolicitudAmpliacionPlazoDomain envio,
                                          boolean remitenteExiste, boolean destinatarioExiste) {
        remitenteExisteRule.validar(new ExistenciaRemitente(envio.getRemitenteUsuario(), remitenteExiste));
        destinatarioExisteRule.validar(
                new ExistenciaDestinatario(envio.getDestinatarioUsuario(), destinatarioExiste));
    }

    @Override
    public void validarAsignacionDestinatario(EnvioSolicitudAmpliacionPlazoDomain envio,
                                              boolean destinatarioAsignado) {
        destinatarioAsignadoRule.validar(new ExistenciaAsignacionResponsable(
                envio.getRemitenteUsuario(), envio.getDestinatarioUsuario(), destinatarioAsignado));
    }

    @Override
    public void validarUnicidad(DisponibilidadSolicitud disponibilidad) {
        solicitudUnicaRule.validar(disponibilidad);
    }
}
