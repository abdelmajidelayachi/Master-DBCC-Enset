<?php
// Démarrer la session
session_start();

if (!isset($_SESSION['panier'])) {
    $_SESSION['panier'] = [];
}

// Vérifier que la requête est bien en POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Récupérer les données du formulaire
    $id = $_POST['id'];
    $title = $_POST['title'];
    $price = $_POST['price'];
    $thumbnail = $_POST['thumbnail'];

    // Vérifier si le produit existe déjà dans le panier
    $produit_existe = false;
    foreach ($_SESSION['panier'] as &$item) {
        if ($item['id'] == $id) {
            $item['quantite']++;
            $produit_existe = true;
            break;
        }
    }
    unset($item); // Casser la référence après le foreach

    // Si le produit n'existe pas, l'ajouter au panier
    if (!$produit_existe) {
        $_SESSION['panier'][] = [
            'id' => $id,
            'title' => $title,
            'price' => $price,
            'thumbnail' => $thumbnail,
            'quantite' => 1
        ];
    }
}

// Rediriger vers la page précédente
header('Location: ' . ($_SERVER['HTTP_REFERER'] ?? '../index.php'));
exit();
