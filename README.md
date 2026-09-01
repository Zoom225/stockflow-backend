# StockFlow Backend

API REST backend pour l'application de gestion de stock StockFlow.

## Technologies

- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Bean Validation
- Lombok

## Projet

StockFlow est une application de gestion de stock pour les petites et moyennes entreprises.

Les fonctionnalites principales prevues sont :

- gestion des produits
- gestion des categories
- gestion des fournisseurs
- entrees et sorties de stock
- historique des mouvements de stock
- alertes de stock faible
- authentification et autorisation
- tableau de bord

## Lancement en local

### 1. Demarrer PostgreSQL

```bash
docker compose up -d
```

Les identifiants par defaut de la base sont :

- Base de donnees : `stockflow`
- Utilisateur : `stockflow`
- Mot de passe : `stockflow`

### 2. Lancer l'application

```bash
./mvnw spring-boot:run
```

Au demarrage, Flyway applique les migrations SQL situees dans `src/main/resources/db/migration` et Hibernate verifie que les mappings des entites correspondent bien au schema.

### 3. Variables d'environnement optionnelles

Tu peux surcharger la connexion par defaut avec :

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

## Endpoints API

### Produits

- `GET /api/products`
- `GET /api/products/low-stock`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

L'endpoint de stock faible retourne les produits pour lesquels `quantityInStock <= minimumStock`.

### Mouvements de stock

- `GET /api/stock-movements`
- `GET /api/stock-movements/{id}`
- `GET /api/stock-movements/products/{productId}`
- `POST /api/stock-movements/products/{productId}/restock`
- `POST /api/stock-movements/products/{productId}/outbound`
- `POST /api/stock-movements`
- `PUT /api/stock-movements/{id}`
- `DELETE /api/stock-movements/{id}`

L'endpoint d'historique produit retourne les mouvements d'un produit tries par `movementDate` decroissante.
L'endpoint de ravitaillement cree un mouvement d'entree en stock et augmente le stock courant du produit.
L'endpoint de sortie cree un mouvement de sortie de stock et diminue le stock courant du produit.
