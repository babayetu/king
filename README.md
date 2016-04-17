King Code
===============

### Start the program

```sh
java -jar kingljj.jar
```

### API
url | description | method | parameters | response
--- | --- | --- | --- | ---
127.0.0.1:18080/1/login | login and get session key | GET | userId=1 | session key |
127.0.0.1:18080/2/score?sessionkey=CWMPPPKPKLTKQOL | update score to level 2 | POST | levelId=2 | nothing or "reject" |
127.0.0.1:18080/2/highscorelist | get highest list of level 2 | GET | levelId=2 | userId=score,...

