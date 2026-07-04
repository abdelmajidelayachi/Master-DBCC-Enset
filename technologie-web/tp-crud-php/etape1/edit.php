<?php
require_once '../config.php';

$pdo = getConnection();

// Récupération de l'utilisateur à modifier
$id = (int) ($_GET['id'] ?? 0);
$stmt = $pdo->prepare('SELECT id, email, role FROM users WHERE id = ?');
$stmt->execute([$id]);
$user = $stmt->fetch();

if (!$user) {
    header('Location: index.php?error=' . urlencode('Utilisateur introuvable.'));
    exit;
}

$errors = [];
$email = $user['email'];
$role = $user['role'];
$roles = ['guest', 'author', 'editor', 'admin'];

// Traitement du formulaire
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = cleanInput($_POST['email'] ?? '');
    $password = $_POST['password'] ?? '';
    $role = $_POST['role'] ?? 'guest';

    // Validation des données
    if (!validateEmail($email)) {
        $errors[] = "L'adresse email n'est pas valide.";
    }
    if ($password !== '' && strlen($password) < 6) {
        $errors[] = 'Le mot de passe doit contenir au moins 6 caractères.';
    }
    if (!in_array($role, $roles, true)) {
        $errors[] = 'Le rôle sélectionné est invalide.';
    }

    // Vérification de l'unicité de l'email (en excluant l'utilisateur courant)
    if (empty($errors)) {
        $stmt = $pdo->prepare('SELECT COUNT(*) FROM users WHERE email = ? AND id != ?');
        $stmt->execute([$email, $id]);
        if ($stmt->fetchColumn() > 0) {
            $errors[] = 'Cette adresse email est déjà utilisée.';
        }
    }

    // Mise à jour de l'utilisateur
    if (empty($errors)) {
        if ($password !== '') {
            // Mot de passe fourni : on le met à jour aussi
            $stmt = $pdo->prepare('UPDATE users SET email = ?, password = ?, role = ? WHERE id = ?');
            $stmt->execute([$email, hashPassword($password), $role, $id]);
        } else {
            // Mot de passe vide : on le conserve
            $stmt = $pdo->prepare('UPDATE users SET email = ?, role = ? WHERE id = ?');
            $stmt->execute([$email, $role, $id]);
        }

        header('Location: index.php?success=updated');
        exit;
    }
}
?>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modifier l'utilisateur</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container py-4">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-warning">
                        <h1 class="h5 mb-0"><i class="bi bi-pencil-square"></i> Modifier l'utilisateur #<?= (int) $user['id'] ?></h1>
                    </div>
                    <div class="card-body">
                        <?php if (!empty($errors)): ?>
                            <div class="alert alert-danger">
                                <ul class="mb-0">
                                    <?php foreach ($errors as $err): ?>
                                        <li><?= htmlspecialchars($err) ?></li>
                                    <?php endforeach; ?>
                                </ul>
                            </div>
                        <?php endif; ?>

                        <form method="post" action="edit.php?id=<?= (int) $user['id'] ?>">
                            <div class="mb-3">
                                <label for="email" class="form-label">Adresse email</label>
                                <input type="email" class="form-control" id="email" name="email"
                                       value="<?= htmlspecialchars($email) ?>" required>
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label">Nouveau mot de passe</label>
                                <input type="password" class="form-control" id="password" name="password" minlength="6">
                                <div class="form-text">Laisser vide pour conserver le mot de passe actuel.</div>
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
                                <button type="submit" class="btn btn-warning">
                                    <i class="bi bi-check-lg"></i> Enregistrer
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
