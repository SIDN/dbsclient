In order to call DBS, you will have to accept its certificate (https). In the browser this is a simple task. We can simply 'trust' an URL. But in Java it works different:

1.	Export the certificate from your browser
-	Start Firefox (browser used in this example)
-	Browse to the DBS server: https://dbs.domain-registry.nl
-	If not done yet: accept its certificate
-	Click on the 'lock' icon in front of the URL and choose ‘more information’
-	Choose ‘View certificate’
-	Choose ‘Details’ and click ‘export’.
-	Next, export as type ‘DER’ and save the file. For example: c:\dbsroot.cer

2.	Import the certificate into your java installation
-	Start a dosbox and type the following command:
keytool -import -keystore %java_home%/jre/lib/security/cacerts -alias dbs -file c:\dbsroot.cer
-	If asked for a password, use the default password ‘changeit’. (or if you changed the password earlier, use your own password)
