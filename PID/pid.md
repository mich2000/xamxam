# Project Xam-Xam

## Probleemstelling

Een probleem dat families vaak hebben is dat ze hun voedselvoorraad slecht beheren. Dit zorgt ervoor dat veel alimentaire producten bederven of dat sommige producten zelfs niet gegeten worden. Ook kunnen ze soms geen producten vinden die zij nodig zouden hebben voor een bepaalde gerecht dat zij zouden willen voorbereiden.

## Doel

De doel van mijn app is de gebruiker de mogelijkheid te geven om producten toe te voegen, te wijzigen of te verwijderen. De producten zullen in een opslagplaats geregistreerd worden. Een opslagplaats kan bv. een koelkast of een kast zijn. Op deze opslagplaatsen kan CRUD op worden toegepast. Opgepast met het verwijderen van een opslagplaats want als deze weg is zijn ook alle producten geassocieerde met deze opslagplaats weg.

De producten zullen ook een status hebben gebasseerd of de producten over de bederfdatum zijn of de bederfdatum er zelfs ingedaan is. De statussen zijn: 
* geel: Voor producten waarvan de bederfdatum hetzelfde is als vandaag.
* Groen: niet bederfde producten
* Rood: bederfde producten
Voor producten die niet zouden kunnen bederven zou de gebruiker deze zelfs niet in de app moeten.

## Werking app

In mijn app heb ik 4 fragmenten en voor de authenticatie en database gebruik ik firebase en firebase firestore. Het 1ste fragment is gebruikt voor de login en registratie's van gebruikers. Firebase zal voor dit deel bijna alles doen, met dit bedoel ik dat de UI voor de login en registratie van firebase zal komen. Voor de authenticatie providers heb ik voor google en voor een basis email/passwoord aanpak gekozen.

Een keer dat de gebruiker is ingelogd wordt hij verstuurde naar de opslagplaats fragment. In deze fragment kan de gebruiker alle opslagplaatsen zien met het aantal producten dat deze bezit. De opslagplaatsen worden ook getoond in een bepaalde kleur om aan te tonen of er misschien slechte producten zijn. Op deze fragment kan men ook naar de profiel fragment gaan en kunnen de statistieken getoond worden in een dialoogvenster. De gebruiker kan de opslagplaatsen verwijderen door een bepaalde opslagplaats te swipen en de naam veranderen door lang erop te klikken. Om de producten te zien van een opslagplaats moet de gebruiker op een opslagplaats klikken.

In de product fragment worden de producten getoond van een bepaalde opslagplaats. De getoonde producten worden in een bepaalde kleur getoond, deze wordt beslist door hun bederfdatum en de datum van vandaag. De gebruiker kan een product toevoegen en een product verwijderen door er eentje te swipen. Een product kan verandert worden door lang erop te klikken. Ook hierop kan de gebruiker de statistieken bekijken.

In de profiel fragment kan de gebruiker zijn naam, passwoord veranderen en kan zelfs zijn profiel verwijderen. Om toegang te hebben tot deze fragment moet de gebruiker zijn email bevestigen als dit niet gedaan werd dan kan hij kiezen om een bevestigingsmail te sturen. Als de mail bevestigt is, moet de gebruiker voor sommige actie's zich herauthenticeren dit is voor het veranderen zijn passwoord en het verwijderen van zijn profiel.

Wanneer de gebruiker zich authenticeert wordt er ook een service opgestart om zijn producten te analyseren en deze te verwittigen wanneer er slechte zijn. In de notificatie zal de gebruiker ook zien welke opslagplaatsen slechte producten bevatten.

## Scope

### In-scope

De design van de stijl van de app en de firebase backend. De enige staat waarmee er rekening gaat gehouden worden is de bederfbaarheid. Het algemene doel van mijn project is een overzicht te geven welke producten waar zijn opgeslagen zijn en niet hun staat zelf.

### Buiten-scope

Andere parameters als bv hun nutritieve waarden worden niet bijgehouden.

## Technische details

### Native

* Deze app is in Kotlin gemaakt.
* Voor de authenticatie en opslag gebruik ik Firebase en Firebase Firstore.
* App maakt gebruik van: 
    * Fragments
    * Recyclerview
    * Data klasses
    * Meer geavanceerde dialoogvensters
    * Services
    * SearchView om te filteren

### Backend (Firebase)

* Gebruik maken outlook of email als authenticatie provider binnen Firebase.
* Voor de database gebruik ik Firebase Firestore.
* Cloud functions voor het verwijderen en toevoegen van gebruikers, hetzelfde doen voor hun respectieve document.

## Deployment (Publiceren van de app)

Als publicatie platform heb ik Amazon app store gekozen, omdat deze gratis is. Bij deze zal ik ook mij logo en een paar screenshots van mijn app publiceren.

## Wat heb ik geleerd ?

* Hoe ik een app in amazon app store kan publiceren.
* Hoe ik een recyclerview kan filteren wanneer een searchview verandert.
* Hoe ik geavanceerde dialoogvensters kan maken en gebruiken.
* Hoe ik een snack kan maken laten tonen en een action te implementeren in zijn button.
* Hoe ik een service kan laten lopen.
* Hoe ik Firebase kan gebruiken voor de authenticatie zijde.
* Document-gebasseerde database van Firebase Firestore.
* Hoe ik cloud functions(serverless functies) kan gebruiken in mijn firebase backend.