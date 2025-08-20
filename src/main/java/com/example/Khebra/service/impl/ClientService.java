package com.example.Khebra.service.impl;

import com.example.Khebra.dto.ClientDto;
import com.example.Khebra.dto.ImageDto;
import com.example.Khebra.entity.Client;
import com.example.Khebra.entity.Image;
import com.example.Khebra.exception.ClientNotFoundException;
import com.example.Khebra.repository.ClientRepository;
import com.example.Khebra.service.IClientService;
import com.example.Khebra.specifications.ClientSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ClientService implements IClientService {

    private final ClientRepository clientRepository;


    public ClientDto getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client not found with email: " + email));

        return mapToClientDto(client);
    }

    @Transactional
    public ClientDto updateClient(ClientDto clientDto) {
        Client client = clientRepository.findByEmail(clientDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Update client fields from DTO
        client.setFirstname(clientDto.getFirstname());
        client.setLastname(clientDto.getLastname());
        client.setAdresse(clientDto.getAdresse());
        client.setInteret(clientDto.getInteret());

        if (clientDto.getImage() != null) {
            if (client.getImage() == null) {
                Image image = new Image();
                image.setUser(client);
                client.setImage(image);
            }
            client.getImage().setImageUrl(clientDto.getImage().getImageUrl());
        }

        clientRepository.save(client); // persist changes

        return mapToClientDto(client);
    }
    public ClientDto getClientById(int id){
        Client client = clientRepository.findClientById(id);
        return mapToClientDto(client);
    }



    public ClientDto mapToClientDto(Client client) {
        ImageDto imageDto = null;
        if (client.getImage() != null) {
            imageDto = ImageDto.builder()
                    .imageUrl(client.getImage().getImageUrl())
                    .build();
        }

        return ClientDto.builder()
                .id(client.getId())
                .firstname(client.getFirstname())
                .lastname(client.getLastname())
                .numTel(client.getNumTel())
                .cin(client.getCin())
                .image(imageDto)
                .email(client.getEmail())
                .adresse(client.getAdresse())
                .interet(client.getInteret())
                .isBanned(client.isBanned())
                .build();
    }

    public Page<ClientDto> getClients(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("firstname").ascending());

            Specification<Client> spec = ClientSpecifications.isActive();

            Page<Client> clientPage = clientRepository.findAll(spec, pageable);

            if (clientPage.isEmpty()) {
                throw new ClientNotFoundException("No clients found with provided filters");
            }

            return clientPage.map(this::mapToClientDto);

        } catch (ClientNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving clients: " + e.getMessage());
        }
    }
}
