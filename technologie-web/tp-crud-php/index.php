<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TP CRUD PHP/MySQL</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container py-5">
        <div class="text-center mb-5">
            <h1><i class="bi bi-database"></i> TP - Opérations CRUD en PHP/MySQL</h1>
            <p class="text-muted">Create, Read, Update, Delete avec PDO et Bootstrap 5</p>
        </div>
        <div class="row g-4 justify-content-center">
            <div class="col-md-4">
                <div class="card h-100 shadow-sm">
                    <div class="card-body text-center">
                        <div class="display-5 mb-2">1</div>
                        <h2 class="h5">CRUD avec fichiers séparés</h2>
                        <p class="text-muted">Un fichier PHP par opération : liste, création, détails, modification, suppression.</p>
                    </div>
                    <div class="card-footer bg-transparent border-0 text-center pb-3">
                        <a href="etape1/" class="btn btn-primary">Ouvrir l'étape 1</a>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card h-100 shadow-sm">
                    <div class="card-body text-center">
                        <div class="display-5 mb-2">2</div>
                        <h2 class="h5">CRUD dans un seul fichier</h2>
                        <p class="text-muted">Toutes les opérations centralisées dans index.php avec routage par paramètres GET.</p>
                    </div>
                    <div class="card-footer bg-transparent border-0 text-center pb-3">
                        <a href="etape2/" class="btn btn-primary">Ouvrir l'étape 2</a>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card h-100 shadow-sm">
                    <div class="card-body text-center">
                        <div class="display-5 mb-2">3</div>
                        <h2 class="h5">CRUD avec AJAX</h2>
                        <p class="text-muted">API REST JSON consommée en JavaScript avec Fetch, modales et notifications.</p>
                    </div>
                    <div class="card-footer bg-transparent border-0 text-center pb-3">
                        <a href="etape3/" class="btn btn-primary">Ouvrir l'étape 3</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
