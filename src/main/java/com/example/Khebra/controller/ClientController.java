package com.example.Khebra.controller;

import com.example.Khebra.dto.ClientDto;
import com.example.Khebra.service.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("client")
@RequiredArgsConstructor
public class ClientController {
    private final IClientService clientService;

    @GetMapping
    public ResponseEntity<ClientDto> getClient(@RequestParam int id) {
      ClientDto client= clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }
    @GetMapping("/all")
    public ResponseEntity<Page<ClientDto>> getExperts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size)
    {
        Page<ClientDto> clientPage = clientService.getClients(page, size);
        return ResponseEntity.ok(clientPage);
    }
}
