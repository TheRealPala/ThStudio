#!/bin/bash

# Definisci il nome del file per le chiavi
KEY_NAME="sign"
KEY_DIR="keys"

# Crea la cartella 'keys' se non esiste
mkdir -p $KEY_DIR

# Genera la chiave privata in formato PEM (PKCS#1)
echo "Generando chiave privata in formato PEM..."
ssh-keygen -t rsa -b 4096 -m PEM -f $KEY_DIR/$KEY_NAME -N ""

# Converte la chiave privata in formato PKCS#8 (se non lo è già)
echo "Convertendo chiave privata in formato PKCS#8..."
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in $KEY_DIR/$KEY_NAME -out $KEY_DIR/${KEY_NAME}_pkcs8.pem

# Converte la chiave pubblica in formato X.509
echo "Generando chiave pubblica in formato X.509..."
openssl rsa -in $KEY_DIR/$KEY_NAME -pubout -out $KEY_DIR/${KEY_NAME}_pub.pem

# Rimuove la chiave privata originale (PKCS#1)
echo "Rimuovendo la chiave privata originale..."
rm $KEY_DIR/$KEY_NAME

# Rinomina i file delle chiavi
echo "Rinomina delle chiavi in corso..."
mv $KEY_DIR/${KEY_NAME}_pkcs8.pem $KEY_DIR/${KEY_NAME}_private.pem
mv $KEY_DIR/${KEY_NAME}_pub.pem $KEY_DIR/${KEY_NAME}_public.pem

# Rimuove il file sign.pub che non è necessario
echo "Rimuovendo il file sign.pub..."
rm $KEY_DIR/${KEY_NAME}.pub

echo "Chiavi generate e rinominate nella cartella '$KEY_DIR':"
echo "Chiave privata: $KEY_DIR/${KEY_NAME}_private.pem"
echo "Chiave pubblica: $KEY_DIR/${KEY_NAME}_public.pem"
