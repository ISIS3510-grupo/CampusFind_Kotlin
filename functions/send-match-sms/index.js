// Functions Framework registra el handler CloudEvent para la función de segunda generación.
// El runtime Node 20, el trigger de creación en matches y las referencias a Secret Manager
// se configuran en el despliegue; este archivo no configura esos recursos.
const f = require("@google-cloud/functions-framework");

/**
 * Atiende el evento configurado y solicita a Twilio el envío de un SMS fijo.
 * event.subject se usa para trazabilidad; no se leen destinatarios del documento matches.
 * Las credenciales llegan como variables de entorno vinculadas a Secret Manager.
 * No deduplica eventos: una entrega repetida puede producir otro SMS.
 */
f.cloudEvent("sendMatchSms", async (event) => {
// Obtiene credenciales y números desde la configuración, sin escribir secretos en el código.
const e = process.env;
// La autenticación Basic usa el SID y el secreto de la API key de Twilio.
const key = e.TWILIO_API_KEY_SID + ":" + e.TWILIO_API_KEY_SECRET;
const url = "https://api.twilio.com/2010-04-01/Accounts/" + e.TWILIO_ACCOUNT_SID + "/Messages.json";
// Envía un formulario al endpoint Messages. Body es el texto literal de la prueba,
// no una selección de plantilla mediante ContentSid. To y From son números configurados.
const res = await fetch(url, {
method: "POST",
headers: {
Authorization: "Basic " + Buffer.from(key).toString("base64"),
"Content-Type": "application/x-www-form-urlencoded"
},
body: new URLSearchParams({ To: e.SMS_TO, From: e.SMS_FROM, Body: "sms_appointment_reminders" })
});
// Registra el estado HTTP y la respuesta de Twilio; no confirma entrega al teléfono.
// Un error HTTP se registra, pero no se lanza aquí para solicitar un reintento explícito.
const data = await res.json();
console.log("match:", event.subject, "twilio:", res.status, data.status || data.message);
});
