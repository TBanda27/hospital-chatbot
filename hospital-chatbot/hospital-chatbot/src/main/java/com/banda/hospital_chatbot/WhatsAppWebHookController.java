package com.banda.hospital_chatbot;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebHookController {

    @PostMapping(value = "/whatsapp", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> handleIncomingMessage(
            @RequestParam(value = "From", required = false) String from,
            @RequestParam(value = "Body", required = false) String body
    ) {
        System.out.println("Incoming WhatsApp message");
        System.out.println("From: " + from);
        System.out.println("Body: " + body);
        System.out.println("-----------------------");

        String phoneNumber = from != null ? from.replace("whatsapp:", "") : "unknown";
        System.out.println("Phone Number: " + phoneNumber);

        String twiml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Response>"
                + "<Message>"
                + phoneNumber + " said: " + body
                + "</Message>"
                + "</Response>";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(twiml);
    }
}
