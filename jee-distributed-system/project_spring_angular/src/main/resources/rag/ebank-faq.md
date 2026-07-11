# E-Bank - Base de connaissances du chatbot

## Presentation de la banque

E-Bank est une banque digitale qui permet de gerer des comptes bancaires en ligne.
Chaque compte bancaire appartient a un client. Un client peut posseder plusieurs comptes.
Les operations bancaires sont consultables a tout moment depuis l'application web
ou aupres du chatbot assistant.

## Types de comptes

E-Bank propose deux types de comptes :

- Le compte courant (Current Account) : destine aux operations du quotidien
  (paiements, virements, retraits). Il dispose d'un decouvert autorise (overdraft),
  c'est-a-dire un montant maximum que le client peut depasser sous zero.
  Le decouvert standard est de 9000 MAD.
- Le compte epargne (Saving Account) : destine a faire fructifier l'argent du client.
  Il est remunere par un taux d'interet annuel (par exemple 5,5 %). Il ne dispose
  pas de decouvert autorise.

## Statuts d'un compte

Un compte peut avoir trois statuts : CREATED (cree, en attente d'activation),
ACTIVATED (actif, toutes les operations sont possibles) et SUSPENDED
(suspendu, les operations sont bloquees jusqu'a regularisation).

## Operations bancaires

Un compte peut subir plusieurs operations, de deux types :

- DEBIT : retrait d'argent du compte, le solde diminue.
- CREDIT : versement d'argent sur le compte, le solde augmente.

Un virement (transfert) est la combinaison d'un DEBIT sur le compte source
et d'un CREDIT sur le compte destination. Un debit est refuse si le solde
du compte est insuffisant.

Chaque operation enregistre sa date, son montant, sa description ainsi que
l'identifiant de l'utilisateur qui l'a effectuee.

## Ouvrir un compte

Pour ouvrir un compte, le client doit s'adresser a un administrateur de la banque
qui cree le client (nom et email) puis le compte (solde initial, decouvert pour
un compte courant ou taux d'interet pour un compte epargne).

## Frais et tarifs

- Tenue de compte courant : 15 MAD par mois.
- Tenue de compte epargne : gratuite.
- Virement interne entre comptes E-Bank : gratuit.
- Virement externe vers une autre banque : 5 MAD par operation.
- Retrait dans les guichets automatiques E-Bank : gratuit.
- Retrait dans les guichets d'autres banques : 6 MAD par retrait.

## Securite

L'acces a l'application est securise par une authentification a base de
jetons JWT (JSON Web Token). Les mots de passe sont stockes de facon chiffree
(BCrypt). Chaque utilisateur peut changer son mot de passe depuis son profil.
Il ne faut jamais communiquer son mot de passe, ni par email ni par telephone :
la banque ne le demande jamais.

## Contact et horaires

Le service client est joignable du lundi au vendredi de 8h30 a 18h00 et le
samedi de 9h00 a 13h00 au +212 5 22 00 00 00 ou par email a support@ebank.ma.
En cas de perte ou de vol de carte, appelez immediatement le numero d'urgence
+212 5 22 99 99 99, disponible 24h/24 et 7j/7.
