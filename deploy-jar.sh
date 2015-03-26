#!/bin/sh
# Deploys a JAR file to your group's cloud server.
# DO NOT MODIFY THIS FILE
GROUP=justtesting
HOSTNAME=sighandsontraining-$GROUP.ydns.eu

if [ "$#" -ne "1" ]
then
    echo "This script must be used to deploy a JAR file to your group's cloud"
    echo "server."
    echo ""
    echo "usage: $0 <jar-file>"
    exit 1
fi

if [ -z "$DEPLOY_KEY" ]
then
    echo "Environment variable DEPLOY_KEY not found!"
    echo "This variable will be set by TravisCI"
    exit 1
fi

cat > /tmp/deploy_id_rsa_enc <<EOT
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
YacNdL6ycOyjNfMera3Wjg==
EOT
openssl aes-256-cbc -k $DEPLOY_KEY -in /tmp/deploy_id_rsa_enc -d -a -out \
/tmp/deploy_id_rsa
chmod 0600 /tmp/deploy_id_rsa
scp -o StrictHostKeyChecking=no -i /tmp/deploy_id_rsa $1 $GROUP@$HOSTNAME:.
rm -f /tmp/deploy_id_rsa
rm -f /tmp/deploy_id_rsa_enc
