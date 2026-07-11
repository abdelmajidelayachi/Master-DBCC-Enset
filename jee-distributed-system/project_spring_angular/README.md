# E-Bank — Gestion de comptes bancaires (Spring Boot + Angular + Chatbot RAG)

Application de gestion de comptes bancaires basée sur un agent AI. Chaque compte
appartient à un client et peut subir des opérations de type `DEBIT` ou `CREDIT`.
Deux types de comptes : **compte courant** (avec découvert) et **compte épargne**
(avec taux d'intérêt).

## Architecture

```
project_spring_angular/
├── src/main/java/dev/elayachi/mbank/   Backend Spring Boot 4 (port 8085)
│   ├── entities/      Customer, BankAccount (héritage SINGLE_TABLE), CurrentAccount,
│   │                  SavingAccount, AccountOperation, AppUser
│   ├── repositories/  Spring Data JPA
│   ├── dtos/          DTOs + records de requêtes
│   ├── mappers/       Mapping entité <-> DTO
│   ├── services/      BankAccountService (métier), UserService (mots de passe)
│   ├── web/           RestControllers (auth, customers, accounts, users, chat)
│   ├── security/      Spring Security + JWT (HS512), UserDetailsService JPA
│   ├── ai/            RagChatService (chatbot RAG OpenAI) + TelegramBotService
│   └── config/        OpenAPI (Swagger), données de démonstration
└── frontend/                            Client Angular 22 (port 4200)
    └── src/app/       login, dashboard (Chart.js), clients, comptes, opérations,
                       chatbot, changement de mot de passe
```

## Lancer le backend

```bash
# Optionnel : chatbot RAG (Google Gemini). La clé peut aussi être mise dans
# secrets.properties (non versionné, voir .gitignore). Ne JAMAIS la commiter.
export GEMINI_API_KEY="..."
# Optionnel : client Telegram du chatbot
export TELEGRAM_BOT_TOKEN="123456:ABC..."

./mvnw spring-boot:run
```

- API : http://localhost:8086
- Swagger UI : http://localhost:8086/swagger-ui.html (bouton *Authorize* pour coller le JWT)
- Console H2 : http://localhost:8086/h2-console (JDBC : `jdbc:h2:mem:bank-db`, user `sa`)

## Lancer le frontend

```bash
cd frontend
npm install
npm start        # http://localhost:4200
```

## Comptes de démonstration

| Utilisateur | Mot de passe | Rôles       |
|-------------|--------------|-------------|
| `admin`     | `12345`      | ADMIN, USER |
| `user1`     | `12345`      | USER        |

Le rôle ADMIN est requis pour créer/modifier/supprimer les clients et créer des
comptes. Des clients, comptes et opérations de démonstration sont générés au
démarrage.

## Sécurité (JWT)

- `POST /auth/login` `{username, password}` → `{accessToken}` (JWT HS512, 2h)
- Toutes les autres routes exigent `Authorization: Bearer <token>`
- Autorisations portées par le claim `scope` (`SCOPE_ADMIN` vérifié via `@PreAuthorize`)
- Mots de passe hachés en BCrypt ; changement via `POST /users/changePassword`
- Chaque client, compte et opération enregistre l'identifiant de l'utilisateur
  authentifié (`createdBy` / `performedBy`)

## Chatbot RAG

`RagChatService` indexe les documents de `src/main/resources/rag/*.md`
(découpage en fragments + embeddings `gemini-embedding-001` en mémoire).
À chaque question, les fragments les plus proches (similarité cosinus) sont
injectés dans le prompt de `gemini-flash-latest`. Le service utilise l'endpoint
Gemini **compatible OpenAI** (`generativelanguage.googleapis.com/v1beta/openai`),
donc changer de fournisseur = changer `ai.api.*` dans `application.properties`.
Clé gratuite : https://aistudio.google.com/apikey. Exposé via :

- `POST /chat` `{message}` → `{response}` (et page *Chatbot* du frontend)
- **Telegram** : `TelegramBotService` fait du long polling sur l'API Bot Telegram
  (`getUpdates`/`sendMessage`) et répond avec le même service RAG. Créer un bot
  via @BotFather puis exporter `TELEGRAM_BOT_TOKEN`.

Sans clé (`GEMINI_API_KEY` ou `secrets.properties`), l'application démarre
normalement et le chatbot répond qu'il n'est pas configuré.

## Tests

```bash
./mvnw test          # couche DAO (@DataJpaTest) + chargement du contexte
cd frontend && npm run build
```

Note : `@DataJpaTest` est configuré avec `spring.test.database.replace=NONE` car
la base embarquée de remplacement de Spring Boot 4.0.7 est créée sans
`DB_CLOSE_DELAY=-1` et disparaît entre deux connexions.
