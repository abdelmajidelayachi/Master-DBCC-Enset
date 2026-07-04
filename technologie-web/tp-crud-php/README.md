# TP CRUD PHP/MySQL

Opérations Create, Read, Update, Delete avec PDO et Bootstrap 5, en 3 étapes :

1. **etape1/** — CRUD avec fichiers séparés (index, create, view, edit, delete)
2. **etape2/** — CRUD dans un seul fichier, routage par `?action=`
3. **etape3/** — CRUD AJAX : API REST JSON (`api.php`) + Fetch API

## Lancer le projet

```bash
# Base de données (Docker)
docker start tp-crud-mysql   # ou la créer :
# docker run -d --name tp-crud-mysql -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -p 3306:3306 mysql:8.4
# docker exec -i tp-crud-mysql mysql -uroot < database.sql

# Serveur web (depuis la racine du projet)
php -S 127.0.0.1:8080
```

Puis ouvrir <http://127.0.0.1:8080/> et choisir une étape.
