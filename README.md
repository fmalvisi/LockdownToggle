# LockdownToggle
## English (per l'Italiano scendere a metà pagina)

source (if you're accessing from outside GitHub): https://github.com/fmalvisi/LockdownToggle

### What is Lockdown mode?

Lockdown mode is a powerful feature in Android devices that engages a screen lock and disables temporarily unlocking the device through biometrics. This could be useful in those cases where your biometrics could be used against your will or coerced from you to unlock your device, from getting mugged to being worried someone might unlock your phone while you're sleeping, etc.

Disclaimer: please exercise common sense and comply with local regulations.

### Enabling Lockdown mode

In most Android devices you can find and enable Lockdown mode by:
- going to Settings
- searching for "Lockdown"
- clicking on Show Lockdown mode option (usually under Secure lock settings)
- enabling the toggle

This allows you to enter lockdown mode from power settings (where you usually shutdown or reboot your device) through a new icon labelled "Lockdown"

### Limitations of Android built-in Lockdown mode and why this app exists

In order to enter Lockdown mode you have to access your power settings, usually by dedicating the long press of your side button or by selecting it from the "power" icon in the Android dropdown menu but:
* some users might want another functionality on the side button long press
* depending on the device and its configuration it might take several seconds to fully enter lockdown mode, this is not ideal
* the "Lockdown" icon/tile in the power settings is not available on the cover screen of Galaxy Z Flip phones and might be similarly unavailable on other foldables in their closed state. Opening the phone takes even longer than the previous point

### What this app offers

- a single page with minimal texts and buttons to grant/remove the permissions required by the app to function and a test button to immediately lock down the device
- an additional icon/tile labelled "Quick Lockdown" that enters Lockdown mode without showing any UI and closing itself right after (minimal footprint). This can be added to the cover screen of foldables (Galaxy Z Flip included, through Good Lock - Multistar)
- an additional Quick Settings tile that enters Lockdown mode directly that can be added to the Android dropdown menu (does not show up in the cover screen dropdown menu of folded phones)

### What this app does NOT do

- ads
- tracking any sort of information for legitimate uses or third party uses

### This app is free and open source

If you want to collaborate I'd be more than happy to have you. What could be useful is:
- add features
- add i18n support for other languages
- make better tests (specifically instrumented ones)

### What permissions does it need?

- Device Administration permissions
- Show lockdown mode option: enabled

In order to function properly, with minimum impact on speed and performance while maximizing security and compatibility with future Android updates this app requires Device Administration permissions, but they are used exclusively to invoke the Lockdown intent (feel free to check the source code).
There are other ways to invoke similar functionalities but are slower and could be interrupted/bypassed (ie: through the Accessibility permissions)

### Why should one use LockdownToggle and not other solutions?

- LockdownToggle is free, does not have ads, does not track your data
- other apps use Accessibility permissions or third-party methods to simulate Lockdown mode which might be less secure and/or less maintainable 
- other apps still do not work on foldable devices in their closed state (do not show up or do not work properly on the cover screen)
- Bixby routines on Samsung phones could achieve a similar result but the Action to directly invoke Lockdown mode is not available in newer versions. An option to just disable specific biometrics is still available (Unlock with Biometrics: off), but it is unclear (to me at least) if it's as secure as enabling Lockdown mode



### Donations

Since this app is free if you would like to donate it would be greatly appreciated but you don't have to.

| Paypal |
| ------ |
| [![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://paypal.me/FMalvisi?country.x=IT&locale.x=en_US) |

## Italiano

sorgenti (se si sta visualizzando questo documento al di fuori di GitHub): https://github.com/fmalvisi/LockdownToggle

### Cos'è la modalità Lockdown?

La modalità Lockdown è una potente funzionalità dei dispositivi Android che attiva il blocco dello schermo e disabilita temporaneamente lo sblocco del dispositivo tramite dati biometrici. Questo può essere utile in situazioni in cui i tuoi dati biometrici potrebbero essere usati contro la tua volontà o sotto coercizione per sbloccare il tuo dispositivo, ad esempio in caso di rapina o se temi che qualcuno possa sbloccare il tuo telefono mentre dormi, ecc.

Disclaimer: Usa il buon senso e a rispetta le normative locali.

### Come attivare la modalità Lockdown

Nella maggior parte dei dispositivi Android puoi trovare e attivare la modalità Lockdown seguendo questi passaggi:
- vai su Impostazioni
- cerca "Lockdown"
- clicca sull'opzione Mostra opzione di blocco (di solito sotto Impostazioni di blocco sicuro)
- abilita l'interruttore

Questo ti permetterà di accedere alla modalità Lockdown dal menu di accensione (dove normalmente spegni o riavvii il dispositivo) tramite una nuova icona chiamata "Lockdown".

### Limitazioni della modalità Lockdown integrata in Android e perché esiste questa app

Per accedere alla modalità Lockdown devi entrare nel menu di accensione, di solito premendo a lungo il tasto laterale o selezionandola dall'icona "accensione" nel menu a discesa di Android, ma:
* alcuni utenti potrebbero voler assegnare un'altra funzionalità alla pressione prolungata del tasto laterale
* a seconda del dispositivo e della configurazione, potrebbero essere necessari diversi secondi per entrare completamente in modalità Lockdown, il che non è l'ideale
* l'icona "Lockdown" nel menu di accensione non è disponibile sullo schermo esterno dei Galaxy Z Flip e potrebbe essere assente anche su altri dispositivi pieghevoli quando sono chiusi. Aprire il telefono richiede ancora più tempo

### Cosa offre questa app

- una singola schermata con testi e pulsanti minimi per concedere/rimuovere i permessi necessari e un pulsante di test per bloccare immediatamente il dispositivo
- un'icona aggiuntiva chiamata "Attivazione Lockdown Immediata" che attiva la modalità Lockdown senza mostrare interfacce grafiche e si chiude subito dopo (impatto minimo). Può essere aggiunta allo schermo esterno dei dispositivi pieghevoli (incluso Galaxy Z Flip, tramite Good Lock - Multistar)
- un'icona aggiuntiva nelle Impostazioni Rapide che attiva direttamente la modalità Lockdown e può essere aggiunta al menu a discesa di Android (non compare però nel menu a discesa dello schermo esterno dei telefoni piegati)

### Cosa NON fa questa app

- pubblicità
- tracciamento di informazioni, né per uso legittimo né per terze parti

### Questa app è gratuita e open source

Se vuoi collaborare sarei più che felice di accoglierti. Ciò che potrebbe essere utile è:
- aggiungere nuove funzionalità
- aggiungere supporto per la traduzione in altre lingue (i18n)
- migliorare i test (in particolare quelli strumentati)

### Quali permessi richiede?

- Permessi di amministrazione del dispositivo
- Interruttore "Mostra opzione di blocco" abilitato

Per funzionare correttamente, con un impatto minimo su velocità e prestazioni e massimizzando la sicurezza e la compatibilità con futuri aggiornamenti Android, l'app richiede i permessi di amministrazione del dispositivo, usati esclusivamente per richiamare l'intento Lockdown (puoi controllare il codice sorgente).
Esistono altri modi per richiamare funzionalità simili ma sono più lenti e possono essere interrotti/aggirati (es: tramite permessi di Accessibilità)

### Perché usare LockdownToggle e non altre soluzioni?

- LockdownToggle è gratuita, senza pubblicità e non traccia i tuoi dati
- altre app usano permessi di Accessibilità o metodi di terze parti per simulare la modalità Lockdown, che potrebbero essere meno sicuri e/o meno sostenibili nel tempo
- altre app ancora non funzionano correttamente sui dispositivi pieghevoli in stato chiuso (non compaiono o non funzionano sullo schermo esterno)
- Le routine Bixby sui telefoni Samsung potrebbero garantire un risultato simile, ma l'azione per richiamare direttamente la modalità Lockdown non è disponibile nelle versioni più recenti delle Routine. Rimane disponibile un'opzione per disabilitare i dati biometrici (Sblocco con dati biometrici: off), ma non è chiaro (almeno a me) se sia sicura quanto abilitare la modalità Lockdown

### Donazioni

Poiché l'app è gratuita, se desideri fare una donazione lo aprrezzerei molto, ma va bene anche se decidi di non farlo.


| Paypal |
| ------ |
| [![](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://paypal.me/FMalvisi?country.x=IT&locale.x=en_US) |
