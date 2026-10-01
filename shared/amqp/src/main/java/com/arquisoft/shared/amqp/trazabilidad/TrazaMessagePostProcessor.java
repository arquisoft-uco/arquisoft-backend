package com.arquisoft.shared.amqp.trazabilidad;

import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.IdentificadorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.TrazaValores;
import com.arquisoft.shared.tracing.infrastructure.traza.propagacion.TrazaHeaders;
import com.arquisoft.shared.util.UtilObjeto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

@RequiredArgsConstructor
public class TrazaMessagePostProcessor implements MessagePostProcessor {

    private final GestorTraza gestorTraza;

    @Override
    public Message postProcessMessage(final Message message) {
        var propiedades = message.getMessageProperties();
        propiedades.setHeader(TrazaHeaders.AMQP_TRACE_ID, UtilObjeto.aplicarPorDefecto(
                gestorTraza.correlacionActual(), IdentificadorTraza.nuevaCorrelacion()));
        propiedades.setHeader(TrazaHeaders.AMQP_TRANSACTION_ID, UtilObjeto.aplicarPorDefecto(
                gestorTraza.transaccionActual(), IdentificadorTraza.nuevaTransaccion()));
        propiedades.setHeader(TrazaHeaders.AMQP_USER_ID, UtilObjeto.aplicarPorDefecto(
                gestorTraza.usuarioActual(), TrazaValores.SISTEMA));
        return message;
    }
}
