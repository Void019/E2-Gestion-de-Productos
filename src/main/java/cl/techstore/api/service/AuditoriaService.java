package cl.techstore.api.service;

import cl.techstore.api.dto.AuditoriaEvento;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class AuditoriaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaService.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String auditQueueUrl;

    public AuditoriaService(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            @Value("${aws.sqs.audit-queue-url:}") String auditQueueUrl) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.auditQueueUrl = auditQueueUrl;
    }

    public void registrarAccion(String accion, Long productoId, String usuario) {
        if (!StringUtils.hasText(auditQueueUrl)) {
            LOGGER.info("Auditoria SQS omitida porque AUDIT_QUEUE_URL no esta configurada");
            return;
        }

        AuditoriaEvento evento = new AuditoriaEvento(accion, productoId, usuario, Instant.now().toString());

        try {
            String cuerpoMensaje = objectMapper.writeValueAsString(evento);
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(auditQueueUrl)
                    .messageBody(cuerpoMensaje)
                    .build();
            sqsClient.sendMessage(request);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("No se pudo serializar el evento de auditoria", ex);
        }
    }
}
