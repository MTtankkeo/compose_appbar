## Create file to .gradle/gradle.properties for User
Must be `gpg --export-secret-keys -o secring.gpg`

```properties
mavenCentralUsername=...
mavenCentralPassword=...

signing.keyId=...
signing.password=...
signing.secretKeyRingFile=...
```