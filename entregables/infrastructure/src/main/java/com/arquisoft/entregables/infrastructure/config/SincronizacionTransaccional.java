package com.arquisoft.entregables.infrastructure.config;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
public class SincronizacionTransaccional {

    private final AppLogger logger;

    public void trasConfirmar(Runnable efecto, String bucket, String clave) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            ejecutar(efecto, bucket, clave);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ejecutar(efecto, bucket, clave);
            }
        });
    }

    public void trasReversion(Runnable efecto, String bucket, String clave) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    ejecutar(efecto, bucket, clave);
                }
            }
        });
    }

    private void ejecutar(Runnable efecto, String bucket, String clave) {
        try {
            efecto.run();
        } catch (RuntimeException e) {
            logger.warn(PaqueteEntregableKey.LOG_EFECTO_DIFERIDO_FALLIDO, bucket, clave);
        }
    }
}
