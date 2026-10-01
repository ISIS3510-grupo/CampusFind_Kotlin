const f = require("@google-cloud/functions-framework");
f.cloudEvent("sendMatchSms", async (event) => {
const e = process.env;
const key = e.TWILIO_API_KEY_SID + ":" + e.TWILIO_API_KEY_SECRET;
const url = "https://api.twilio.com/2010-04-01/Accounts/" + e.TWILIO_ACCOUNT_SID + "/Messages.json";
const res = await fetch(url, {
method: "POST",
headers: {
Authorization: "Basic " + Buffer.from(key).toString("base64"),
"Content-Type": "application/x-www-form-urlencoded"
},
body: new URLSearchParams({ To: e.SMS_TO, From: e.SMS_FROM, Body: "sms_appointment_reminders" })
});
const data = await res.json();
console.log("match:", event.subject, "twilio:", res.status, data.status || data.message);
});
