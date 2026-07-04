<?php
require_once '../config.php';

// API REST JSON : toutes les réponses sont au format JSON
header('Content-Type: application/json; charset=utf-8');

$pdo = getConnection();
$roles = ['guest', 'author', 'editor', 'admin'];
$method = $_SERVER['REQUEST_METHOD'];

// Envoie une réponse JSON avec le code HTTP donné puis arrête le script
function jsonResponse($data, $status = 200) {
    http_response_code($status);
    echo json_encode($data, JSON_UNESCAPED_UNICODE);
    exit;
}

// Récupère un utilisateur par id (sans le mot de passe)
function findUser($pdo, $id) {
    $stmt = $pdo->prepare('SELECT id, email, role, created_at, updated_at FROM users WHERE id = ?');
    $stmt->execute([$id]);
    return $stmt->fetch();
}

// Validation des données d'un utilisateur (create / update)
function validateUserData($pdo, $email, $password, $role, $roles, $excludeId = null, $passwordRequired = true) {
    $errors = [];
    if (!validateEmail($email)) {
        $errors[] = "L'adresse email n'est pas valide.";
    }
    if (($passwordRequired || $password !== '') && strlen($password) < 6) {
        $errors[] = 'Le mot de passe doit contenir au moins 6 caractères.';
    }
    if (!in_array($role, $roles, true)) {
        $errors[] = 'Le rôle sélectionné est invalide.';
    }
    if (empty($errors)) {
        $sql = 'SELECT COUNT(*) FROM users WHERE email = ?';
        $params = [$email];
        if ($excludeId !== null) {
            $sql .= ' AND id != ?';
            $params[] = $excludeId;
        }
        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        if ($stmt->fetchColumn() > 0) {
            $errors[] = 'Cette adresse email est déjà utilisée.';
        }
    }
    return $errors;
}

// Corps de la requête pour PUT (JSON)
function getJsonBody() {
    $body = json_decode(file_get_contents('php://input'), true);
    return is_array($body) ? $body : [];
}

switch ($method) {
    // ---------- READ : liste complète ou un seul utilisateur ----------
    case 'GET':
        if (isset($_GET['id'])) {
            $user = findUser($pdo, (int) $_GET['id']);
            if (!$user) {
                jsonResponse(['success' => false, 'message' => 'Utilisateur introuvable.'], 404);
            }
            jsonResponse(['success' => true, 'data' => $user]);
        }
        $users = $pdo->query('SELECT id, email, role, created_at FROM users ORDER BY id')->fetchAll();
        jsonResponse(['success' => true, 'data' => $users]);

    // ---------- CREATE ----------
    case 'POST':
        $body = getJsonBody();
        $email = cleanInput($body['email'] ?? '');
        $password = $body['password'] ?? '';
        $role = $body['role'] ?? 'guest';

        $errors = validateUserData($pdo, $email, $password, $role, $roles);
        if (!empty($errors)) {
            jsonResponse(['success' => false, 'errors' => $errors], 400);
        }

        $stmt = $pdo->prepare('INSERT INTO users (email, password, role) VALUES (?, ?, ?)');
        $stmt->execute([$email, hashPassword($password), $role]);

        jsonResponse([
            'success' => true,
            'message' => 'Utilisateur créé avec succès.',
            'data' => findUser($pdo, (int) $pdo->lastInsertId()),
        ], 201);

    // ---------- UPDATE ----------
    case 'PUT':
        $body = getJsonBody();
        $id = (int) ($body['id'] ?? ($_GET['id'] ?? 0));

        if (!findUser($pdo, $id)) {
            jsonResponse(['success' => false, 'message' => 'Utilisateur introuvable.'], 404);
        }

        $email = cleanInput($body['email'] ?? '');
        $password = $body['password'] ?? '';
        $role = $body['role'] ?? 'guest';

        $errors = validateUserData($pdo, $email, $password, $role, $roles, $id, false);
        if (!empty($errors)) {
            jsonResponse(['success' => false, 'errors' => $errors], 400);
        }

        if ($password !== '') {
            $stmt = $pdo->prepare('UPDATE users SET email = ?, password = ?, role = ? WHERE id = ?');
            $stmt->execute([$email, hashPassword($password), $role, $id]);
        } else {
            $stmt = $pdo->prepare('UPDATE users SET email = ?, role = ? WHERE id = ?');
            $stmt->execute([$email, $role, $id]);
        }

        jsonResponse([
            'success' => true,
            'message' => 'Utilisateur modifié avec succès.',
            'data' => findUser($pdo, $id),
        ]);

    // ---------- DELETE ----------
    case 'DELETE':
        $id = (int) ($_GET['id'] ?? 0);

        if (!findUser($pdo, $id)) {
            jsonResponse(['success' => false, 'message' => 'Utilisateur introuvable.'], 404);
        }

        $stmt = $pdo->prepare('DELETE FROM users WHERE id = ?');
        $stmt->execute([$id]);

        jsonResponse(['success' => true, 'message' => 'Utilisateur supprimé avec succès.']);

    // ---------- Méthode non supportée ----------
    default:
        jsonResponse(['success' => false, 'message' => 'Méthode non autorisée.'], 405);
}
