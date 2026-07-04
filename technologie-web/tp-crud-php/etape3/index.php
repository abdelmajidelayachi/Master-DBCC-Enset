<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TP CRUD PHP/MySQL - Étape 3 (AJAX)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        #loading { display: none; }
        body.loading #loading { display: inline-block; }
    </style>
</head>
<body>
    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <span class="navbar-brand">
                <i class="bi bi-people-fill"></i> TP CRUD - Étape 3 (AJAX)
            </span>
            <div class="spinner-border spinner-border-sm text-light" id="loading" role="status"></div>
        </div>
    </nav>

    <div class="container pb-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h1 class="h4 mb-0">Liste des utilisateurs</h1>
            <button class="btn btn-primary" onclick="openCreateModal()">
                <i class="bi bi-person-plus"></i> Nouvel utilisateur
            </button>
        </div>

        <div class="card shadow-sm">
            <div class="card-body">
                <p class="text-muted">Nombre total d'utilisateurs : <strong id="userCount">0</strong></p>
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
                        <tbody id="usersTable"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Modale de création / modification -->
    <div class="modal fade" id="userModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form id="userForm" novalidate>
                    <div class="modal-header">
                        <h5 class="modal-title" id="userModalTitle">Nouvel utilisateur</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="formErrors" class="alert alert-danger d-none"></div>
                        <input type="hidden" id="userId">
                        <div class="mb-3">
                            <label for="email" class="form-label">Adresse email</label>
                            <input type="email" class="form-control" id="email" required>
                            <div class="invalid-feedback">Adresse email invalide.</div>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label" id="passwordLabel">Mot de passe</label>
                            <input type="password" class="form-control" id="password" minlength="6">
                            <div class="form-text" id="passwordHelp">Au moins 6 caractères.</div>
                            <div class="invalid-feedback">Le mot de passe doit contenir au moins 6 caractères.</div>
                        </div>
                        <div class="mb-3">
                            <label for="role" class="form-label">Rôle</label>
                            <select class="form-select" id="role">
                                <option value="guest">guest</option>
                                <option value="author">author</option>
                                <option value="editor">editor</option>
                                <option value="admin">admin</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                        <button type="submit" class="btn btn-primary" id="userModalSubmit">Créer</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modale de confirmation de suppression -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title text-danger">Confirmer la suppression</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    Voulez-vous vraiment supprimer l'utilisateur <strong id="deleteEmail"></strong> ?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                    <button type="button" class="btn btn-danger" id="confirmDelete">
                        <i class="bi bi-trash"></i> Supprimer
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Conteneur des notifications toast -->
    <div class="toast-container position-fixed bottom-0 end-0 p-3" id="toastContainer"></div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const API_URL = 'api.php';
        const ROLE_COLORS = { admin: 'danger', editor: 'warning', author: 'info', guest: 'secondary' };

        const userModal = new bootstrap.Modal(document.getElementById('userModal'));
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        let deleteTarget = null;

        // Échappe le HTML pour éviter les injections XSS côté client
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        // Affiche une notification toast Bootstrap
        function showToast(message, type = 'success') {
            const container = document.getElementById('toastContainer');
            const toast = document.createElement('div');
            toast.className = `toast align-items-center text-bg-${type} border-0`;
            toast.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body">${escapeHtml(message)}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>`;
            container.appendChild(toast);
            const bsToast = new bootstrap.Toast(toast, { delay: 4000 });
            bsToast.show();
            toast.addEventListener('hidden.bs.toast', () => toast.remove());
        }

        // Appel générique à l'API avec indicateur de chargement
        async function apiRequest(url, options = {}) {
            document.body.classList.add('loading');
            try {
                const response = await fetch(url, options);
                const data = await response.json();
                if (!response.ok) {
                    const message = data.errors ? data.errors.join(' ') : (data.message || 'Erreur inconnue.');
                    throw new Error(message);
                }
                return data;
            } finally {
                document.body.classList.remove('loading');
            }
        }

        // ---------- READ : chargement et affichage de la liste ----------
        async function loadUsers() {
            try {
                const { data } = await apiRequest(API_URL);
                document.getElementById('userCount').textContent = data.length;
                const tbody = document.getElementById('usersTable');
                if (data.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Aucun utilisateur trouvé.</td></tr>';
                    return;
                }
                tbody.innerHTML = data.map(user => `
                    <tr>
                        <td>${user.id}</td>
                        <td>${escapeHtml(user.email)}</td>
                        <td><span class="badge bg-${ROLE_COLORS[user.role] || 'secondary'}">${escapeHtml(user.role)}</span></td>
                        <td>${escapeHtml(user.created_at)}</td>
                        <td class="text-end">
                            <button class="btn btn-sm btn-outline-warning" title="Modifier"
                                    onclick="openEditModal(${user.id})">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger" title="Supprimer"
                                    onclick="openDeleteModal(${user.id}, '${escapeHtml(user.email)}')">
                                <i class="bi bi-trash"></i>
                            </button>
                        </td>
                    </tr>`).join('');
            } catch (error) {
                showToast(error.message, 'danger');
            }
        }

        // ---------- CREATE : ouverture de la modale vide ----------
        function openCreateModal() {
            document.getElementById('userForm').reset();
            document.getElementById('userId').value = '';
            document.getElementById('userModalTitle').textContent = 'Nouvel utilisateur';
            document.getElementById('userModalSubmit').textContent = 'Créer';
            document.getElementById('passwordLabel').textContent = 'Mot de passe';
            document.getElementById('passwordHelp').textContent = 'Au moins 6 caractères.';
            document.getElementById('password').required = true;
            clearFormErrors();
            userModal.show();
        }

        // ---------- UPDATE : ouverture de la modale pré-remplie ----------
        async function openEditModal(id) {
            try {
                const { data } = await apiRequest(`${API_URL}?id=${id}`);
                document.getElementById('userForm').reset();
                document.getElementById('userId').value = data.id;
                document.getElementById('email').value = data.email;
                document.getElementById('role').value = data.role;
                document.getElementById('userModalTitle').textContent = `Modifier l'utilisateur #${data.id}`;
                document.getElementById('userModalSubmit').textContent = 'Enregistrer';
                document.getElementById('passwordLabel').textContent = 'Nouveau mot de passe';
                document.getElementById('passwordHelp').textContent = 'Laisser vide pour conserver le mot de passe actuel.';
                document.getElementById('password').required = false;
                clearFormErrors();
                userModal.show();
            } catch (error) {
                showToast(error.message, 'danger');
            }
        }

        function clearFormErrors() {
            document.getElementById('formErrors').classList.add('d-none');
            document.getElementById('userForm').classList.remove('was-validated');
        }

        // Soumission du formulaire (création ou modification)
        document.getElementById('userForm').addEventListener('submit', async (event) => {
            event.preventDefault();

            // Validation côté client
            const form = event.target;
            if (!form.checkValidity()) {
                form.classList.add('was-validated');
                return;
            }

            const id = document.getElementById('userId').value;
            const payload = {
                email: document.getElementById('email').value,
                password: document.getElementById('password').value,
                role: document.getElementById('role').value,
            };

            try {
                let result;
                if (id) {
                    // Modification : requête PUT
                    result = await apiRequest(API_URL, {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ id: Number(id), ...payload }),
                    });
                } else {
                    // Création : requête POST
                    result = await apiRequest(API_URL, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload),
                    });
                }
                userModal.hide();
                showToast(result.message);
                await loadUsers();
            } catch (error) {
                const errorsDiv = document.getElementById('formErrors');
                errorsDiv.textContent = error.message;
                errorsDiv.classList.remove('d-none');
            }
        });

        // ---------- DELETE : confirmation puis suppression ----------
        function openDeleteModal(id, email) {
            deleteTarget = id;
            document.getElementById('deleteEmail').textContent = email;
            deleteModal.show();
        }

        document.getElementById('confirmDelete').addEventListener('click', async () => {
            try {
                const result = await apiRequest(`${API_URL}?id=${deleteTarget}`, { method: 'DELETE' });
                deleteModal.hide();
                showToast(result.message);
                await loadUsers();
            } catch (error) {
                deleteModal.hide();
                showToast(error.message, 'danger');
            }
        });

        // Chargement initial de la liste
        loadUsers();
    </script>
</body>
</html>
