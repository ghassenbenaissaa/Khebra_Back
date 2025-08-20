package com.example.Khebra.specifications;

import com.example.Khebra.entity.Client;
import org.springframework.data.jpa.domain.Specification;

public class ClientSpecifications {
    public static Specification<Client> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("IsActive"));
    }
}
