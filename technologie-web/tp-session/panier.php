<?php
// Inclure le header
include 'includes/header.php';

// Initialiser une variable $total à 0
$total = 0;
?>

<div class="container my-4">
    <h1 class="mb-4">Mon Panier</h1>

    <?php if (empty($_SESSION['panier'])): ?>
        <div class="alert alert-info">
            Votre panier est vide. <a href="index.php" class="alert-link">Voir nos produits</a>
        </div>
    <?php else: ?>
        <div class="table-responsive">
            <table class="table align-middle">
                <thead>
                    <tr>
                        <th>Produit</th>
                        <th>Prix unitaire</th>
                        <th>Quantité</th>
                        <th>Sous-total</th>
                    </tr>
                </thead>
                <tbody>
                    <?php foreach ($_SESSION['panier'] as $article): ?>
                        <?php
                        $sous_total = $article['price'] * $article['quantite'];
                        $total += $sous_total;
                        ?>
                        <tr>
                            <td>
                                <img src="<?php echo htmlspecialchars($article['thumbnail']); ?>"
                                     alt="<?php echo htmlspecialchars($article['title']); ?>"
                                     width="50" height="50" style="object-fit: cover;" class="me-2 rounded">
                                <?php echo htmlspecialchars($article['title']); ?>
                            </td>
                            <td><?php echo number_format($article['price'], 2); ?> €</td>
                            <td><?php echo $article['quantite']; ?></td>
                            <td><?php echo number_format($sous_total, 2); ?> €</td>
                        </tr>
                    <?php endforeach; ?>
                </tbody>
                <tfoot>
                    <tr>
                        <th colspan="3" class="text-end">Total :</th>
                        <th><?php echo number_format($total, 2); ?> €</th>
                    </tr>
                </tfoot>
            </table>
        </div>
    <?php endif; ?>

    <div class="d-flex justify-content-between mt-4">
        <a href="index.php" class="btn btn-secondary">
            <i class="bi bi-arrow-left"></i> Continuer les achats
        </a>
        <?php if (!empty($_SESSION['panier'])): ?>
            <form method="POST" action="actions/vider_panier.php">
                <button type="submit" class="btn btn-danger">
                    <i class="bi bi-trash"></i> Vider le panier
                </button>
            </form>
        <?php endif; ?>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
