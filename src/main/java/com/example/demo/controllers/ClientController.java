// ======= CLIENT CONTROLLER =======
package com.example.demo.controllers;

import com.example.demo.entities.ClientEntity;
import com.example.demo.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/clients")
@CrossOrigin("*")  // Permite CORS (útil para frontends)
public class ClientController {

    @Autowired
    private ClientService clientService;

    // GET ALL (ahora con ResponseEntity y List en vez de ArrayList)
    @GetMapping("/")
    public ResponseEntity<List<ClientEntity>> getAllClients() {
        List<ClientEntity> clients = clientService.getClients();
        return ResponseEntity.ok(clients);  // 200 OK
    }

    // POST (con validación implícita en ResponseEntity)
    @PostMapping("/")
    public ResponseEntity<ClientEntity> createClient(@RequestBody ClientEntity client) {
        ClientEntity savedClient = clientService.saveClient(client);
        return ResponseEntity.ok(savedClient);
    }

    // GET BY RUT (manejo explícito de HTTP status)
    @GetMapping("/{rut}")
    public ResponseEntity<ClientEntity> getClient(@PathVariable String rut) {
        ClientEntity client = clientService.getClientByRut(rut);
        return ResponseEntity.ok(client);
    }

    // PUT (similar al POST pero para actualizar)
    @PutMapping("/")
    public ResponseEntity<ClientEntity> updateClient(@RequestBody ClientEntity client) {
        ClientEntity updatedClient = clientService.updateClient(client);
        return ResponseEntity.ok(updatedClient);
    }

    // DELETE (retorna 204 No Content como buena práctica)
    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> deleteClient(@PathVariable String rut) throws Exception {
        clientService.deleteClient(rut);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}