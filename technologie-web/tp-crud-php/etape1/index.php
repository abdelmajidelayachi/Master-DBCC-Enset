<?php
require_once '../config.php';

// Connexion à la base de données
$pdo = getConnection();

// Récupération de tous les utilisateurs
$stmt = $pdo->query('SELECT id, email, role, created_at FROM users ORDER BY id');
$users = $stmt->fetchAll();

// Messages flash transmis via GET
$messages = [
    'created' => 'Utilisateur créé avec succès.',
    'updated' => 'Utilisateur modifié avec succès.',
    'deleted' => 'Utilisateur supprimé avec succès.',
];
$success = isset($_GET['success']) && isset($messages[$_GET['success']]) ? $messages[$_GET['success']] : null;
$error = isset($_GET['error']) ? cleanInput($_GET['error']) : null;

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
    <title>TP CRUD PHP/MySQL - Étape 1</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0"><i class="bi bi-people-fill"></i> Liste des utilisateurs</h1>
            <a href="create.php" class="btn btn-primary">
                <i class="bi bi-person-plus"></i> Nouvel utilisateur
            </a>
        </div>

        <?php if ($success): ?>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> <?= htmlspecialchars($success) ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <?php endif; ?>

        <?php if ($error): ?>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> <?= htmlspecialchars($error) ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <?php endif; ?>

        <div class="card shadow-sm">
            <div class="card-body">
                <p class="text-muted">Nombre total d'utilisateurs : <strong><?= count($users) ?></strong></p>

                <?php if (empty($users)): ?>
                    <div class="alert alert-info mb-0">Aucun utilisateur trouvé.</div>
                <?php else: ?>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Email</th>
                                    <th>Rôle</th>
                                    <th>Créé le</th>
                                    <th class="text-end">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <?php foreach ($users as $user): ?>
                                    <tr>
                                        <td><?= (int) $user['id'] ?></td>
                                        <td><?= htmlspecialchars($user['email']) ?></td>
                                        <td>
                                            <span class="badge bg-<?= roleBadge($user['role']) ?>">
                                                <?= htmlspecialchars($user['role']) ?>
                                            </span>
                                        </td>
                                        <td><?= htmlspecialchars($user['created_at']) ?></td>
                                        <td class="text-end">
                                            <a href="view.php?id=<?= (int) $user['id'] ?>" class="btn btn-sm btn-outline-info" title="Voir">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                            <a href="edit.php?id=<?= (int) $user['id'] ?>" class="btn btn-sm btn-outline-warning" title="Modifier">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <a href="delete.php?id=<?= (int) $user['id'] ?>" class="btn btn-sm btn-outline-danger" title="Supprimer"
                                               onclick="return confirm('Voulez-vous vraiment supprimer cet utilisateur ?');">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </td>
                                    </tr>
                                <?php endforeach; ?>
                            </tbody>
                        </table>
                    </div>
                <?php endif; ?>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
