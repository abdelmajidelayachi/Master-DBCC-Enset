<?php
require_once '../config.php';

$pdo = getConnection();

// Rôles autorisés et action demandée (routage par paramètre GET)
$roles = ['guest', 'author', 'editor', 'admin'];
$action = $_GET['action'] ?? 'list';
$id = (int) ($_GET['id'] ?? 0);

$errors = [];
$user = null;

// Couleur du badge selon le rôle
function roleBadge($role) {
    $colors = ['admin' => 'danger', 'editor' => 'warning', 'author' => 'info', 'guest' => 'secondary'];
    return $colors[$role] ?? 'secondary';
}

// Récupère un utilisateur ou redirige vers la liste s'il n'existe pas
function findUserOrRedirect($pdo, $id) {
    $stmt = $pdo->prepare('SELECT id, email, role, created_at, updated_at FROM users WHERE id = ?');
    $stmt->execute([$id]);
    $user = $stmt->fetch();
    if (!$user) {
        header('Location: index.php?error=' . urlencode('Utilisateur introuvable.'));
        exit;
    }
    return $user;
}

// Validation commune aux actions create et edit
function validateUserForm($pdo, $email, $password, $role, $roles, $excludeId = null, $passwordRequired = true) {
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

// Traitement des actions (avant tout affichage pour permettre les redirections)
switch ($action) {
    case 'create':
        $email = '';
        $role = 'guest';
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $email = cleanInput($_POST['email'] ?? '');
            $password = $_POST['password'] ?? '';
            $role = $_POST['role'] ?? 'guest';

            $errors = validateUserForm($pdo, $email, $password, $role, $roles);
            if (empty($errors)) {
                $stmt = $pdo->prepare('INSERT INTO users (email, password, role) VALUES (?, ?, ?)');
                $stmt->execute([$email, hashPassword($password), $role]);
                header('Location: index.php?success=created');
                exit;
            }
        }
        break;

    case 'view':
        $user = findUserOrRedirect($pdo, $id);
        break;

    case 'edit':
        $user = findUserOrRedirect($pdo, $id);
        $email = $user['email'];
        $role = $user['role'];
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $email = cleanInput($_POST['email'] ?? '');
            $password = $_POST['password'] ?? '';
            $role = $_POST['role'] ?? 'guest';

            $errors = validateUserForm($pdo, $email, $password, $role, $roles, $id, false);
            if (empty($errors)) {
                if ($password !== '') {
                    $stmt = $pdo->prepare('UPDATE users SET email = ?, password = ?, role = ? WHERE id = ?');
                    $stmt->execute([$email, hashPassword($password), $role, $id]);
                } else {
                    $stmt = $pdo->prepare('UPDATE users SET email = ?, role = ? WHERE id = ?');
                    $stmt->execute([$email, $role, $id]);
                }
                header('Location: index.php?success=updated');
                exit;
            }
        }
        break;

    case 'delete':
        findUserOrRedirect($pdo, $id);
        $stmt = $pdo->prepare('DELETE FROM users WHERE id = ?');
        $stmt->execute([$id]);
        header('Location: index.php?success=deleted');
        exit;

    default:
        $action = 'list';
        $stmt = $pdo->query('SELECT id, email, role, created_at FROM users ORDER BY id');
        $users = $stmt->fetchAll();
        break;
}

// Messages flash transmis via GET
$messages = [
    'created' => 'Utilisateur créé avec succès.',
    'updated' => 'Utilisateur modifié avec succès.',
    'deleted' => 'Utilisateur supprimé avec succès.',
];
$success = isset($_GET['success']) && isset($messages[$_GET['success']]) ? $messages[$_GET['success']] : null;
$flashError = isset($_GET['error']) ? cleanInput($_GET['error']) : null;
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TP CRUD PHP/MySQL - Étape 2</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <a class="navbar-brand" href="index.php">
                <i class="bi bi-people-fill"></i> TP CRUD - Étape 2 (fichier unique)
            </a>
        </div>
    </nav>

    <div class="container pb-4">
        <?php if ($success): ?>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle"></i> <?= htmlspecialchars($success) ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <?php endif; ?>

        <?php if ($flashError): ?>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle"></i> <?= htmlspecialchars($flashError) ?>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <?php endif; ?>

        <?php if (!empty($errors)): ?>
            <div class="alert alert-danger">
                <ul class="mb-0">
                    <?php foreach ($errors as $err): ?>
                        <li><?= htmlspecialchars($err) ?></li>
                    <?php endforeach; ?>
                </ul>
            </div>
        <?php endif; ?>

        <?php if ($action === 'list'): ?>
            <!-- ==================== LISTE ==================== -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h1 class="h4 mb-0">Liste des utilisateurs</h1>
                <a href="index.php?action=create" class="btn btn-primary">
                    <i class="bi bi-person-plus"></i> Nouvel utilisateur
                </a>
            </div>
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
                                    <?php foreach ($users as $u): ?>
                                        <tr>
                                            <td><?= (int) $u['id'] ?></td>
                                            <td><?= htmlspecialchars($u['email']) ?></td>
                                            <td>
                                                <span class="badge bg-<?= roleBadge($u['role']) ?>">
                                                    <?= htmlspecialchars($u['role']) ?>
                                                </span>
                                            </td>
                                            <td><?= htmlspecialchars($u['created_at']) ?></td>
                                            <td class="text-end">
                                                <a href="index.php?action=view&id=<?= (int) $u['id'] ?>" class="btn btn-sm btn-outline-info" title="Voir">
                                                    <i class="bi bi-eye"></i>
                                                </a>
                                                <a href="index.php?action=edit&id=<?= (int) $u['id'] ?>" class="btn btn-sm btn-outline-warning" title="Modifier">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <a href="index.php?action=delete&id=<?= (int) $u['id'] ?>" class="btn btn-sm btn-outline-danger" title="Supprimer"
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

        <?php elseif ($action === 'create' || $action === 'edit'): ?>
            <!-- ==================== FORMULAIRE CREATE / EDIT ==================== -->
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="card shadow-sm">
                        <div class="card-header <?= $action === 'create' ? 'bg-primary text-white' : 'bg-warning' ?>">
                            <h1 class="h5 mb-0">
                                <?php if ($action === 'create'): ?>
                                    <i class="bi bi-person-plus"></i> Créer un utilisateur
                                <?php else: ?>
                                    <i class="bi bi-pencil-square"></i> Modifier l'utilisateur #<?= (int) $user['id'] ?>
                                <?php endif; ?>
                            </h1>
                        </div>
                        <div class="card-body">
                            <form method="post" action="index.php?action=<?= $action ?><?= $action === 'edit' ? '&id=' . (int) $user['id'] : '' ?>">
                                <div class="mb-3">
                                    <label for="email" class="form-label">Adresse email</label>
                                    <input type="email" class="form-control" id="email" name="email"
                                           value="<?= htmlspecialchars($email) ?>" required>
                                </div>
                                <div class="mb-3">
                                    <label for="password" class="form-label">
                                        <?= $action === 'create' ? 'Mot de passe' : 'Nouveau mot de passe' ?>
                                    </label>
                                    <input type="password" class="form-control" id="password" name="password"
                                           minlength="6" <?= $action === 'create' ? 'required' : '' ?>>
                                    <div class="form-text">
                                        <?= $action === 'create' ? 'Au moins 6 caractères.' : 'Laisser vide pour conserver le mot de passe actuel.' ?>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label for="role" class="form-label">Rôle</label>
                                    <select class="form-select" id="role" name="role">
                                        <?php foreach ($roles as $r): ?>
                                            <option value="<?= $r ?>" <?= $r === $role ? 'selected' : '' ?>><?= $r ?></option>
                                        <?php endforeach; ?>
                                    </select>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <a href="index.php" class="btn btn-secondary">
                                        <i class="bi bi-arrow-left"></i> Retour
                                    </a>
                                    <button type="submit" class="btn <?= $action === 'create' ? 'btn-primary' : 'btn-warning' ?>">
                                        <i class="bi bi-check-lg"></i> <?= $action === 'create' ? 'Créer' : 'Enregistrer' ?>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

        <?php elseif ($action === 'view'): ?>
            <!-- ==================== DÉTAILS ==================== -->
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
                                <a href="index.php?action=edit&id=<?= (int) $user['id'] ?>" class="btn btn-warning">
                                    <i class="bi bi-pencil"></i> Modifier
                                </a>
                                <a href="index.php?action=delete&id=<?= (int) $user['id'] ?>" class="btn btn-danger"
                                   onclick="return confirm('Voulez-vous vraiment supprimer cet utilisateur ?');">
                                    <i class="bi bi-trash"></i> Supprimer
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        <?php endif; ?>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
