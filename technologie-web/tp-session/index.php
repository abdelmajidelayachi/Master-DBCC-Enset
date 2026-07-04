<?php
// Inclure le fichier header.php
include 'includes/header.php';

// Récupérer les produits depuis l'API
$json = file_get_contents('https://dummyjson.com/products?limit=30');
$data = json_decode($json, true);
$produits = $data['products'] ?? [];
?>

<div class="container my-4">
    <h1 class="mb-4">Nos Produits</h1>

    <div class="row">
        <?php foreach ($produits as $produit): ?>
            <div class="col-md-6 col-lg-4 mb-4">
                <div class="card h-100">
                    <img src="<?php echo htmlspecialchars($produit['thumbnail']); ?>"
                         class="card-img-top"
                         style="height: 200px; object-fit: cover;"
                         alt="<?php echo htmlspecialchars($produit['title']); ?>">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title"><?php echo htmlspecialchars($produit['title']); ?></h5>
                        <p class="card-text"><?php echo htmlspecialchars($produit['description']); ?></p>
                        <div class="d-flex justify-content-between align-items-center mt-auto">
                            <span class="h5 mb-0"><?php echo number_format($produit['price'], 2); ?> €</span>
                            <form method="POST" action="actions/ajouter_panier.php">
                                <input type="hidden" name="id" value="<?php echo htmlspecialchars($produit['id']); ?>">
                                <input type="hidden" name="title" value="<?php echo htmlspecialchars($produit['title']); ?>">
                                <input type="hidden" name="price" value="<?php echo htmlspecialchars($produit['price']); ?>">
                                <input type="hidden" name="thumbnail" value="<?php echo htmlspecialchars($produit['thumbnail']); ?>">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-cart-plus"></i> Ajouter
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        <?php endforeach; ?>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
