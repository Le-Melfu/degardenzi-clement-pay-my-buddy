package com.paymybuddy.config;

// TODO: Implémenter rate limiting - Créer la configuration pour la protection contre les attaques par force brute
// - Limiter les tentatives de connexion
// - Limiter les requêtes par IP
// - Limiter les créations de transactions
// - Utiliser Bucket4j ou Spring Boot Actuator

import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitingConfig {

    // TODO: Configurer le rate limiting pour les endpoints sensibles
    // - /login : max 5 tentatives par minute
    // - /register : max 3 créations par heure
    // - /transaction : max 10 transactions par minute
    // - Endpoints généraux : max 100 requêtes par minute

    // TODO: Implémenter la logique de rate limiting
    // TODO: Ajouter les tests pour valider le rate limiting
    // TODO: Configurer les messages d'erreur appropriés
    // TODO: Implémenter la whitelist pour les IPs de confiance
}
