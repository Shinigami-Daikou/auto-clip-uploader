# auto-clip-uploader

A Quarkus based project that trims a video into clips and upload it on YouTube.

### Prerequisite
* JDK-21
* Maven
* A [Google Cloud Project](https://console.cloud.google.com/) with Youtube Data API and OAuth 2.0 enabled. Visit [Here](https://developers.google.com/youtube/v3/quickstart/java)

### Setup
* Clone the Repository
* Create an API Key and OAuth 2.0 client ID for YouTube API. Visit [Here](https://developers.google.com/youtube/v3/quickstart/java)
* Download the JSON file that contains your OAuth 2.0 credentials. The file has a name like client_secret_CLIENTID.json, where CLIENTID is the client ID for your project. 
* Rename this file to <code>client_secret.json</code> and place it in <code>/src/main/resources</code> folder.
* Fill the values in <code>application.yml<code>
* Build the application and run the runner_jar.