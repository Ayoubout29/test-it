# Notes TD

Bugs trouves avec les tests :

- au debut aucun test Spring ne marchait, l'appli demarrait pas : il manquait constraintName dans les fichiers liquibase, la table s'appelait members au lieu de member, la colonne user au lieu de username, referent_id etait en uuid et Book etait en SEQUENCE alors que la table est en auto increment
- borrowBook n'envoyait pas de mail au membre
- borrowBook cherchait les livres RENDU au lieu de EMPRUNTE pour savoir si le membre a deja un livre
- la requete findByMemberAndStatus avait un OR au lieu de AND (elle renvoyait aussi les livres de bob)
- au retour le mail du referent etait envoye au membre
- n'importe qui pouvait rendre un livre, le memberId n'etait pas verifie
- erreur LazyInitializationException dans returnBook, j'ai mis @Transactional
- dans le controller createBook mettait le titre a la place de l'auteur, et /borrow appelait returnBook
- le json plantait apres un emprunt, j'ai mis @JsonIgnore sur borrower, books et referent
- emprunter sans etre connecte donnait une erreur 500 au lieu de 400

Pas fait : le PUT sur un livre emprunte remet le statut a DISPONIBLE
