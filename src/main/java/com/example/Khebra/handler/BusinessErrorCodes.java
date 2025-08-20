package com.example.Khebra.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
@RequiredArgsConstructor
@Getter
public enum BusinessErrorCodes {
    // Generic / infra
    NO_CODE( HttpStatus.NOT_IMPLEMENTED, "No code"),

    // Auth
    INCORRECT_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH( HttpStatus.BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "Votre compte est en cour de verification par nos admins."),
    ACCOUNT_DISABLED( HttpStatus.UNAUTHORIZED, "User account is disabled"),
    ACCOUNT_BANNED( HttpStatus.FORBIDDEN, "Account is banned"),
    BAD_CREDENTIALS( HttpStatus.UNAUTHORIZED, "Email et/ou mot de passe incorrect"),
    ACCOUNT_NOT_ACTIVATED( HttpStatus.FORBIDDEN, "Compte non activé, un nouveau message de vérification a été envoyé à votre adresse e-mail."),
    REFRESH_TOKEN_EXPIRED( HttpStatus.UNAUTHORIZED, "Token expired"),

    EXPERT_NOT_VALIDATED( HttpStatus.FORBIDDEN, "Expert account not yet validated"),
    USER_NOT_EXIST( HttpStatus.NOT_FOUND, "Cet email n'existe pas"),
    // Domain
    EXPERT_NOT_FOUND(HttpStatus.NOT_FOUND, "Aucun expert trouver"),

    // Validation / Messaging
    VALIDATION_FAILED( HttpStatus.BAD_REQUEST, "Validation failed"),
    MESSAGING_FAILURE( HttpStatus.INTERNAL_SERVER_ERROR, "Email delivery failed"),
    DOMAIN_NOT_FOUND( HttpStatus.NOT_FOUND, "Domain not found"),
    FILE_SIZE( PAYLOAD_TOO_LARGE, "la taille du fichier dépasse la limite"),
    FILE_TYPE_NOT_SUPPORTED( HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Ce type de fichier n'est pas valide"),


    // ==== Registration / Uniqueness ====
    EMAIL_ALREADY_USED( HttpStatus.CONFLICT, "Email déja utiliser"),
    CIN_ALREADY_USED( HttpStatus.CONFLICT, "CIN existe déja"),
    ADDRESS_ALREADY_USED( HttpStatus.CONFLICT, "Address is already in use"), // only if truly needed
    DUPLICATE_RESOURCE( HttpStatus.CONFLICT, "Duplicate resource found."),


    //Activation email errors
    CODE_EXPIRED( HttpStatus.GONE, "code d'activation est expriré, veuillez vérifier votre email."),
    CODE_USED(HttpStatus.GONE,"Code d'activation est déja utiliser."),
    CODE_NOT_FOUND( HttpStatus.NOT_FOUND, "Code d'activation n'existe pas.");


    private final HttpStatus httpStatus;
    private final String description;
}