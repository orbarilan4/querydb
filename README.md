# QueryDB

QueryDB is a microservice, that gets query and exports the query's results to a CSV file.

## 🏆 prerequisites
1. Docker
2. cURL


## ⚡ Build & Run

1. Use ```git clone https://github.com/orbarilan4/querydb.git``` to clone a repository into a new directory.

2. Put ```config.conf``` (your jdbc configuration file) in ```querydb``` dir (aside to the ```Dockerfile```).  
   The file should look like:  
   ```
   jdbc {
        url = "jdbc:mysql://mydb.net/querydbtest"
   
        username = "username"
   
        password = "password"
   }
   ```
   
3. Go to ```querydb``` dir by doing the command:  
```
cd querydb
```

4. Create ```shadowJar``` with the command:
```
gradlew sJ
```

5. Build an image from the Dockerfile:
```
docker build -t my-dockerized-java-app:v1 .
```

6. Create and start container:
```
docker run --mount type=bind,source="Your-Output-File-Path",target=/output -p 8080:8080 --name webserver my-dockerized-java-app:v1
```
## 📐 CURL for testing
The Microservice request body will be as follow:
```
  {
    "query" : "select * from table where id > 3"
    "fileName" : "testdata.csv"
  }
```
Then the cURL command should look like:
```
curl -H "Content-type: application/json" -X POST -d '{"query": "select * from table where id > 3","fileName":" testdata.csv"}' http://localhost:8080/
```

![alt text](https://www.mememaker.net/static/images/memes/4792340.jpg)