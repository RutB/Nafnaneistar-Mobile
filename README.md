# Mobile throbo

Heimasíða:
http://www.nafnaneistar.xyz/

Allar bakgrunns upplýsingar, uml, user stories ofl. er geymt í möppunni **Skjöl**

# Skipting á möppum í Repo
## nafnaneistar
Inniheldur bakendann eða þá Springboot verkefni sem byggist á verkefninu fyrir Hugbúnaðarverkefni 1. Hægt er að opna þá möppu í texta ritli, t.d. vscode eða öðrum ritli sem er með stuðning fyrir springboot verkefni og keyra
við keyrslu ræsist bakendi á porti 7979 sem tekur við beiðnum.
(ATH að sá bakendi er einnig ræstur á http://46.22.102.179:7979 og hægt er að senda beiðnir þangað)

## nafnaneistarApp
Inniheldur Android Studio verkefnið og viðeiandi skrár til að opna on keyra verkefnið í Android Studio.

## Skjöl
Inniheldur skilaverkefnin tengd Hugbúnaðarverkefni 2 áfanganum

## Annað
möppur eins og out, .vscode og dist má bara hunsa og eru ekki nauðsynlegar fyrir verkefnið.


# Keyrsla
Það þarf að vera með á vélinni sem keyrir forritið Java 11 eða nýrra.
Það ætti að vera hægt bara að clone-a repoið eða sækja repo-ið og keyra í Android Studio.
Ekki þarf að setja upp bakenda þar sem hann keyrir á http://46.22.102.179:7979 eins og sést í ApiController

# Ef notaður er local bakendi
Þá er hægt að keyra springboot möppuna sem er undir Nafnaneistar möppunni
Forritið keyrir sjálfgefið á http://localhost:7979/ og taka skal eftir að það er port 7979 svo ef að það port er í notkun þarf að slökkva á þeirri þjónustu sem er keyrandi þar.
* ef að repo-ið er sótt er hægt að breyta sjálfgefnu porti í application.properties skránni og breyta línunni sem inniheldur: server.port=7979 og breyta þá 7979 í eitthvað annað.

Það þarf að setja http://localhost:7979/ á form með IP tölunni eins og sést er í domainURL í ApiController

Það ætti að vera hægt bara að clone-a repoið eða sækja repo-ið og keyra í VsCode með springboot extension pakkanum eða í öðrum studdum ritlum (t.d. Eclipse).

# Þekktir kvillar:
 - Takkar á leita af nafni og tengja á partnera fara yfir valmyndarstikuna
 - Það á eftir að útfæra útlit á flestu í Landscape
 - Það á eftir að birta leitarniðurstöður í lista, sést aðeins í console
 - vantar að búa til bil í tengja partner milli nafns og netfang

# Virkni sem er komin fyrir code review
  - Öll virkni er í portrait mode
  - Innskráning
  - Nýskráning
  - Velja nöfn
    - Swipe hægri/vinstri
    - Kynja val
  - Tengja saman partnera
    - Tengir partnera saman
    - Sýnir lista af tengdum partnerum
  - Valin nöfn
    - Sýnir fjölda samþykktra nafna
    - Nafna samsetningar


