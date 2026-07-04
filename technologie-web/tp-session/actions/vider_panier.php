<?php
// Démarrer la session
session_start();

// Vider le panier
$_SESSION['panier'] = [];

// Rediriger vers la page du panier
header('Location: ../panier.php');
exit();
