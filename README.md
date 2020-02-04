#  BlockCV - Blockchain project using Hyperledger Fabric  #

BlockCV è un progetto open-source che ha per scopo la creazione di un sistema distribuito
basato su tecnologia Permissioned Blockchain atto alla pubblicazione di CV e alla ricerca di proposte di lavoro.




#  Installazione  #

L'installazione e la compilazione del prodotto BlockCV sono state testate sui seguenti sistemi operativi:
- Ubuntu 16.04 64bit

## Prerequisiti ##

Prerequisiti per l'installazione:

- installare curl 7.59.0
- installare Docker in versione 17.06.2-ce o successiva
- installare Docker Compose in versione 1.14.0 o successiva
- Node.js in versione 8.9.x or successive [necessario solo in caso si voglia utilizzare blockchain-explorer]

per maggiori informazioni, visitare: http://hyperledger-fabric.readthedocs.io/en/release-1.1/prereqs.html


## Guida all'installazione: ##

## Configurazione e avvio di Fabric ##

- eseguire lo script: software\blockcv-blockchain\network\download-binaries.sh
- eseguire lo script: software\blockcv-blockchain\network\download-images.sh
- eseguire lo script: software\blockcv-blockchain\network\start.sh

## Avvio BlockCV ##

- importare il progetto Maven software\blockcv-client con un IDE a piacere
- impostare una run configuration con i seguenti comandi:
	mvn:compile
	jetty:run







