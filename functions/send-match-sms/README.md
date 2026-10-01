# Envío de SMS por coincidencia

La función se dispara al crearse un documento en la colección `matches` de Firestore y envía un SMS con Twilio.

Las credenciales viven en Secret Manager con los nombres `twilio-account-sid`, `twilio-api-key-sid` y `twilio-api-key-secret`. Nunca se suben al repositorio.

El número de origen se configura en `SMS_FROM` y el destino en `SMS_TO`, como variables de entorno.

Para desplegar, ejecuta desde `functions/send-match-sms` este comando, sustituyendo los marcadores por los números correspondientes:

```sh
gcloud functions deploy send-match-sms --gen2 --runtime=nodejs20 --region=us-central1 --source=. --entry-point=sendMatchSms --trigger-location=nam5 --trigger-event-filters="type=google.cloud.firestore.document.v1.created" --trigger-event-filters="database=(default)" --trigger-event-filters-path-pattern="document=matches/{matchId}" --set-secrets=TWILIO_API_KEY_SID=twilio-api-key-sid:latest,TWILIO_API_KEY_SECRET=twilio-api-key-secret:latest,TWILIO_ACCOUNT_SID=twilio-account-sid:latest --set-env-vars=SMS_FROM=<NUMERO_TWILIO>,SMS_TO=<NUMERO_DESTINO>
```
