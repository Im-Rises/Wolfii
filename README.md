# Wolfii

Programme Android développé sous Android Studio pour la lecture de musique.

## Explication du programme, page de code par page de code

### MainActivity

ici on initialise le bottom navigation, le mService et la database.

objets statics :
mService => notre service qui fonctionne en arrière plan
database => notre bdd pour recup les playlists
des tableaux => remplient la methode "getMusic" avec un curseur

### NouveauteFragment

on verifie que mesGenres contain download, hidden music, liked music
on init un recyclerview

### ClickOnGenre

quand on click sur Genre => les actions

### mobile_navigation 

on affiche toutes les pages du bottom nav et on definit la page principale.
on affecte un id, avec un layout et son code java pour chaque fragment.

### MusiqueService

lire en arriere plan des musiques, gerer la playlist