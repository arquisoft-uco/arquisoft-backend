package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.transaccion;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EjecutorTransaccionalOutputPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
public class EjecutorTransaccionalOutputAdapter implements EjecutorTransaccionalOutputPort {

    private final TransactionTemplate lectura;
    private final TransactionTemplate escritura;

    public EjecutorTransaccionalOutputAdapter(
            @Qualifier("entregablesTransactionManager") PlatformTransactionManager transactionManager) {
        this.lectura = new TransactionTemplate(transactionManager);
        this.lectura.setReadOnly(true);
        this.escritura = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T enLectura(Supplier<T> accion) {
        return lectura.execute(estado -> accion.get());
    }

    @Override
    public <T> T enEscritura(Supplier<T> accion) {
        return escritura.execute(estado -> accion.get());
    }
}
