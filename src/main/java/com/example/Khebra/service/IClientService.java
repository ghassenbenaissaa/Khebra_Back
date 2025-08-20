package com.example.Khebra.service;

import com.example.Khebra.dto.ClientDto;
import org.springframework.data.domain.Page;

import java.util.List;


public interface IClientService {

    ClientDto getClientByEmail(String email);
    ClientDto getClientById(int id);
    ClientDto updateClient(ClientDto clientDto);
    Page<ClientDto> getClients(int page, int size);

}
