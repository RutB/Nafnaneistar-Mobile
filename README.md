# Mobile throbo

Heimasíða:
http://www.nafnaneistar.xyz/

Allar bakgrunns upplýsingar, uml, user stories ofl. er geymt í möppunni **Skjöl**

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


