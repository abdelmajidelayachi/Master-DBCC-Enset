<?php
// Configuration de la base de données
define('DB_HOST', '127.0.0.1');
define('DB_NAME', 'tp_crud_php');
define('DB_USER', 'root');
define('DB_PASS', '');
define('DB_CHARSET', 'utf8mb4');

// Fonction de connexion à la base de données
function getConnection() {
    try {
        $dsn = 'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=' . DB_CHARSET;
        $pdo = new PDO($dsn, DB_USER, DB_PASS);
        
        // Configuration PDO
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
        $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
        
        return $pdo;
    } catch (PDOException $e) {
        die('Erreur de connexion : ' . $e->getMessage());
    }
}

// Fonction pour hasher un mot de passe
function hashPassword($password) {
    return password_hash($password, PASSWORD_DEFAULT);
}

// Fonction pour vérifier un mot de passe
function verifyPassword($password, $hash) {
    return password_verify($password, $hash);
}

// Fonction pour valider l'email
function validateEmail($email) {
    return filter_var($email, FILTER_VALIDATE_EMAIL);
}

// Redirige /dossier vers /dossier/ pour que les liens relatifs fonctionnent
// (le serveur PHP intégré sert l'index sans ajouter le slash final)
function ensureTrailingSlash() {
    $path = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
    $dir = rtrim(dirname($_SERVER['SCRIPT_NAME']), '/');
    if ($dir !== '' && $path === $dir) {
        $query = $_SERVER['QUERY_STRING'] ?? '';
        header('Location: ' . $path . '/' . ($query !== '' ? '?' . $query : ''), true, 301);
        exit;
    }
}

// Fonction pour nettoyer les données
function cleanInput($data) {
    $data = trim($data);
    $data = stripslashes($data);
    $data = htmlspecialchars($data);
    return $data;
}
?>
