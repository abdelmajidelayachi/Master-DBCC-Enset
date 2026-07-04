<?php
require_once '../config.php';

$pdo = getConnection();

// Récupération de l'id à supprimer
$id = (int) ($_GET['id'] ?? 0);

// Vérification que l'utilisateur existe
$stmt = $pdo->prepare('SELECT id FROM users WHERE id = ?');
$stmt->execute([$id]);

if (!$stmt->fetch()) {
    header('Location: index.php?error=' . urlencode('Utilisateur introuvable.'));
    exit;
}

// Suppression de l'utilisateur
$stmt = $pdo->prepare('DELETE FROM users WHERE id = ?');
$stmt->execute([$id]);

header('Location: index.php?success=deleted');
exit;
