<?php
require_once '../config.php';

$pdo = getConnection();

// Récupération de l'utilisateur par son id
$id = (int) ($_GET['id'] ?? 0);
$stmt = $pdo->prepare('SELECT id, email, role, created_at, updated_at FROM users WHERE id = ?');
$stmt->execute([$id]);
$user = $stmt->fetch();

// Utilisateur introuvable : retour à la liste avec un message d'erreur
if (!$user) {
    header('Location: index.php?error=' . urlencode('Utilisateur introuvable.'));
    exit;
}

// Couleur du badge selon le rôle
function roleBadge($role) {
    $colors = ['admin' => 'danger', 'editor' => 'warning', 'author' => 'info', 'guest' => 'secondary'];
    return $colors[$role] ?? 'secondary';
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détails de l'utilisateur</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container py-4">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-info text-white">
                        <h1 class="h5 mb-0"><i class="bi bi-person-badge"></i> Détails de l'utilisateur</h1>
                    </div>
                    <div class="card-body">
                        <table class="table table-borderless mb-0">
                            <tr>
                                <th class="w-25">ID</th>
                                <td><?= (int) $user['id'] ?></td>
                            </tr>
                            <tr>
                                <th>Email</th>
                                <td><?= htmlspecialchars($user['email']) ?></td>
                            </tr>
                            <tr>
                                <th>Rôle</th>
                                <td>
                                    <span class="badge bg-<?= roleBadge($user['role']) ?>">
                                        <?= htmlspecialchars($user['role']) ?>
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <th>Créé le</th>
                                <td><?= htmlspecialchars($user['created_at']) ?></td>
                            </tr>
                            <tr>
                                <th>Modifié le</th>
                                <td><?= htmlspecialchars($user['updated_at']) ?></td>
                            </tr>
                        </table>
                    </div>
                    <div class="card-footer d-flex justify-content-between">
                        <a href="index.php" class="btn btn-secondary">
                            <i class="bi bi-arrow-left"></i> Retour
                        </a>
                        <div>
                            <a href="edit.php?id=<?= (int) $user['id'] ?>" class="btn btn-warning">
                                <i class="bi bi-pencil"></i> Modifier
                            </a>
                            <a href="delete.php?id=<?= (int) $user['id'] ?>" class="btn btn-danger"
                               onclick="return confirm('Voulez-vous vraiment supprimer cet utilisateur ?');">
                                <i class="bi bi-trash"></i> Supprimer
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
