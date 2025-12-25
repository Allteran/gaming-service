# Project structure and description
Project is designed using hexagonal architecture. To achieve decoupling between modules we use layers:
- **domain**: contains business entities and state of that entities
- **app**: contains main business logic and interfaces to operate
- **adapter**: contains actual implementation of interfaces and business logic using Quarkus and different libs & approaches (e.g. internal database instead of external one like SQL).

# The flow
![img.png](img.png)
1. User (Person) choses game on Partner's side.
2. Partner generates JWT on his side where it puts platform ID, user ID and calls https://our.domain/game/{gameId}/launch
3. Our frontend displays loading and proxy that request to the backend.
4. game-service (our backend) decodes JWT and using encoded data checks the database for Platform and does it allow to play particular game.
   4.1. In case if platform was blocked on our side - we return 403 error
   4.2. In case if we do not have info about such platform - we return 401 error
   4.3. In case of invalid JWT we return 403.
5. In case if everything is OK game-service make request to the Platform's backend to identify player and get player's info: balance, currency, etc.
   5.1. If Platform returns 404 or any other error - we propagate it as 401 from our side.
6. After retrieving player's info game-service generates game session and stores it inside NoSQL database.
7. Game-service generates internal JWT which will be using only on our side by player and returns sessionId and JWT to the frontend.
8. Using generated JWT and sessionId user can make actual requests to play the game directly through our frontend to the game-service.
9. After each game action we send update to the Provider's backend about player's balance.
10. On our side we have WebHook to listen about player's event updates: balance update, blocking player, session ends, etc.
# How to run:
1. On root project level (game-service) run ```mvn clean install```
2. From adapter fonder run ```mvn quarkus:dev```
3. Separately run from demo-external-jwt-issuer ```mvn quarkus:dev```
# How to use:
There are 3 endpoints to use from all the modules:\
For issuing external token use endpoint from demo-external-jwt-issuer:
```curl -X POST http://localhost:8081/api/platform```
with request body: 
```json
{
    "playerId": "c8185ca2-9975-4ef6-bc5e-83c3ec3b4f3c",
    "platformId": "6fe96fed-8998-46f8-a2f8-3234f90df452"
}
```
As result you will get external JWT which should be used for launching game.

For launching game use endpoint from game-service (adapter):
```curl -X POST http://localhost:8080/api/game/{gameId}/launch?token={externalJWT}```
As the result you will get sessionId and internal JWT which should be used for playing game.

For playing game use endpoint from game-service (adapter) with Bearer token:
```curl -X POST http://localhost:8080/api/v1/game/play?sessionId={sessionId}```
As result you will get game response from a game platform
# Pre-configured data:
## Platforms & allowed games:
1. JoyCasino (platformId: 6fe96fed-8998-46f8-a2f8-3234f90df452), **active**. Games:
   - IndianaJohnSlits (gameId: 100)
    - Roller (gameId: 101)
    - CoolBall (gameId: 102)
2. FreeBetCasino (platformId: 0af4aced-2386-4f92-91b7-71cf6d070eec), **active**. Games:
    - IndianaJohnSlits (gameId: 100)
    - CoolBall (gameId: 102)
3. BlockedCasino (platformId: 20652211-babd-4994-b3b7-101da062d463), **inactive**. Games:
    - IndianaJohnSlits (gameId: 100)
    - Roller (gameId: 101)
    - CoolBall (gameId: 102)
## Before running you will have to generate 
# Tests
## Accessing from platform to the game that is allowed
1. Requesting token from demo-external-jwt-issuer:
```
curl --location 'localhost:8081/api/platform' \
--header 'Content-Type: application/json' \
--data '{
    "playerId": "c8185ca2-9975-4ef6-bc5e-83c3ec3b4f3c",
    "platformId": "6fe96fed-8998-46f8-a2f8-3234f90df452"
}'
```
The result:
```json
eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiI2ZmU5NmZlZC04OTk4LTQ2ZjgtYTJmOC0zMjM0ZjkwZGY0NTIiLCJzdWIiOiJjODE4NWNhMi05OTc1LTRlZjYtYmM1ZS04M2MzZWMzYjRmM2MiLCJncm91cHMiOlsiVVNFUiJdLCJleHAiOjE3NjY2ODA4NjEsImlhdCI6MTc2NjY3NzI2MSwianRpIjoiMTVjNjI5NjUtM2QyNi00NTY5LWFjMWMtOTlkOTc1YTEwODlmIn0.CnMwfoYt93KjsmPm6kbSc-K_eJMBteaBPSQCj3DGz7IHM5HF-fxwt_tEkSyPf3tmlTs9YAvj-E794L5qVej_h07ejcLiM-g0HFvJQM-gh4q6pL0bJXk_sVfmr8tM_oYP4xuTlEbd6NFY-tm35Z7NBR9GrzZlSMNvImJ9TxiyohdCWQgwcfR1z5A8QcMy5Pb-VNFeqjLZI6t5WC4mc6t_FC0LFTCYfkM7pVWVK47oQ9Hyx9H1OMXUjbUAaHJM2B5-7MZQG99TmirP-3D2bTGPOB_ME9BQE1hJ5sTO-AoyxhVth1LGLg7a7VLVVvoWOZ91VK9-ejnuAa7oEdrrkzUaqw
```
2. Launching game:
```
curl --location --request POST 'localhost:8080/api/v1/game/100/launch?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiI2ZmU5NmZlZC04OTk4LTQ2ZjgtYTJmOC0zMjM0ZjkwZGY0NTIiLCJzdWIiOiJjODE4NWNhMi05OTc1LTRlZjYtYmM1ZS04M2MzZWMzYjRmM2MiLCJncm91cHMiOlsiVVNFUiJdLCJleHAiOjE3NjY2ODA4NjEsImlhdCI6MTc2NjY3NzI2MSwianRpIjoiMTVjNjI5NjUtM2QyNi00NTY5LWFjMWMtOTlkOTc1YTEwODlmIn0.CnMwfoYt93KjsmPm6kbSc-K_eJMBteaBPSQCj3DGz7IHM5HF-fxwt_tEkSyPf3tmlTs9YAvj-E794L5qVej_h07ejcLiM-g0HFvJQM-gh4q6pL0bJXk_sVfmr8tM_oYP4xuTlEbd6NFY-tm35Z7NBR9GrzZlSMNvImJ9TxiyohdCWQgwcfR1z5A8QcMy5Pb-VNFeqjLZI6t5WC4mc6t_FC0LFTCYfkM7pVWVK47oQ9Hyx9H1OMXUjbUAaHJM2B5-7MZQG99TmirP-3D2bTGPOB_ME9BQE1hJ5sTO-AoyxhVth1LGLg7a7VLVVvoWOZ91VK9-ejnuAa7oEdrrkzUaqw'
```
The result:
```json
{
    "internalToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3lvdXIuZG9tYWluLmlvLyIsInN1YiI6ImM4MTg1Y2EyLTk5NzUtNGVmNi1iYzVlLTgzYzNlYzNiNGYzYyIsInBsYXRmb3JtSWQiOiI2ZmU5NmZlZC04OTk4LTQ2ZjgtYTJmOC0zMjM0ZjkwZGY0NTIiLCJnYW1lSWQiOjEwMCwiaWF0IjoxNzY2Njc3Mzk2LCJleHAiOjE3NjY2ODA5OTYsImdyb3VwcyI6WyJVU0VSIl0sImp0aSI6IjcxNjlhNzgyLTczMmQtNGE1Ni1iMTU2LWVjNjczMjMyNTE0NCJ9.P4QfS8FJapzOgpyICx2O1UZjWJEsV3aSIR2IDKZrYtHdLhKz1sf5SulODgUND8cecI7xrHCwpDjjEU1DpX-5SuYglH_2LsNX5OSM4FKjIwxOowmUqDplXnZ48Nw16tT_e5jin0SPzWp7UCAwxK3qyzORY1ydOCdub_Tu9ok7_xPgKOSh30lPtWSuCmmivJHYyK1kiEoyDcdIgoAW2K54yJ6szZppsOCJXGIcKPzxylAqyZ5OgtPLgOENwWLfGg2yoCc8_nQ6uVhRCxPKq6HBHBiJ7NHrkA0i13x6-SA1p9td_cq5tOybm9iu_Wbl5Ik06ha__rrAn7KWs_8cCZp3ew",
    "sessionId": "6751b162-3683-474a-a787-1307cc21f301"
}
```
3. Playing the game:
```
curl --location --request POST 'localhost:8080/api/v1/game/play?sessionId=6751b162-3683-474a-a787-1307cc21f301' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3lvdXIuZG9tYWluLmlvLyIsInN1YiI6ImM4MTg1Y2EyLTk5NzUtNGVmNi1iYzVlLTgzYzNlYzNiNGYzYyIsInBsYXRmb3JtSWQiOiI2ZmU5NmZlZC04OTk4LTQ2ZjgtYTJmOC0zMjM0ZjkwZGY0NTIiLCJnYW1lSWQiOjEwMCwiaWF0IjoxNzY2Njc3Mzk2LCJleHAiOjE3NjY2ODA5OTYsImdyb3VwcyI6WyJVU0VSIl0sImp0aSI6IjcxNjlhNzgyLTczMmQtNGE1Ni1iMTU2LWVjNjczMjMyNTE0NCJ9.P4QfS8FJapzOgpyICx2O1UZjWJEsV3aSIR2IDKZrYtHdLhKz1sf5SulODgUND8cecI7xrHCwpDjjEU1DpX-5SuYglH_2LsNX5OSM4FKjIwxOowmUqDplXnZ48Nw16tT_e5jin0SPzWp7UCAwxK3qyzORY1ydOCdub_Tu9ok7_xPgKOSh30lPtWSuCmmivJHYyK1kiEoyDcdIgoAW2K54yJ6szZppsOCJXGIcKPzxylAqyZ5OgtPLgOENwWLfGg2yoCc8_nQ6uVhRCxPKq6HBHBiJ7NHrkA0i13x6-SA1p9td_cq5tOybm9iu_Wbl5Ik06ha__rrAn7KWs_8cCZp3ew'
```
The result:
```json
{
    "gameEvent": {
        "id": "b476e7ca-69be-4d5c-93b8-e14457bed4ca",
        "sessionId": "6751b162-3683-474a-a787-1307cc21f301",
        "gameId": 100,
        "playerId": "c8185ca2-9975-4ef6-bc5e-83c3ec3b4f3c",
        "platformId": "6fe96fed-8998-46f8-a2f8-3234f90df452",
        "amount": 100,
        "type": "WIN"
    }
}
```
## Accessing from platform to the game that is NOT allowed
1. Requesting token from demo-external-jwt-issuer:
```
curl --location 'localhost:8081/api/platform' \
--header 'Content-Type: application/json' \
--data '{
    "playerId": "c8185ca2-9975-4ef6-bc5e-83c3ec3b4f3c",
    "platformId": "0af4aced-2386-4f92-91b7-71cf6d070eec"
}'
```
As result:
```json
eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiIwYWY0YWNlZC0yMzg2LTRmOTItOTFiNy03MWNmNmQwNzBlZWMiLCJzdWIiOiJjODE4NWNhMi05OTc1LTRlZjYtYmM1ZS04M2MzZWMzYjRmM2MiLCJncm91cHMiOlsiVVNFUiJdLCJleHAiOjE3NjY2ODEzMjIsImlhdCI6MTc2NjY3NzcyMiwianRpIjoiMTkzZmZlYzMtZTg2Yi00ODI2LTgwZmEtZjJkODgyMzA2YzFiIn0.og9_HURVCH_ytjxxA9AcEzX92mnkN5lD23kd4lAEfJRwDExi-OUCNgwnZgv55j9wZn-ANnt5_xdsbE_Vm_CryBb_weAeUIsjje2vu7uCNh9UNLsqIQtDCbKWTMI8FIbJBrKTfEryVu4f_98YNZeP3Qq8e9-w4QqgpgSZCd2eyHTGTUUdItK_5Q_qEqpp9UlKFiZgQ6kwAAmI0J6DJGPypGaqevm11vUUPhyW-U053bBbsIwib_0f3r9pGrpDxBDQmUvzkZu1GuDNhWwvs5xt2X24TLwmoT8BD59RG3LBWvtIQSdd2WQSWdva1q3r6uDnL3cp0NyKp2sYgXU2yr-OXQ
```
2. Launching game (should throw an error because game with ID 101 is not allowed for such platform):
```
curl --location --request POST 'localhost:8080/api/v1/game/101/launch?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiIwYWY0YWNlZC0yMzg2LTRmOTItOTFiNy03MWNmNmQwNzBlZWMiLCJzdWIiOiJjODE4NWNhMi05OTc1LTRlZjYtYmM1ZS04M2MzZWMzYjRmM2MiLCJncm91cHMiOlsiVVNFUiJdLCJleHAiOjE3NjY2ODEzMjIsImlhdCI6MTc2NjY3NzcyMiwianRpIjoiMTkzZmZlYzMtZTg2Yi00ODI2LTgwZmEtZjJkODgyMzA2YzFiIn0.og9_HURVCH_ytjxxA9AcEzX92mnkN5lD23kd4lAEfJRwDExi-OUCNgwnZgv55j9wZn-ANnt5_xdsbE_Vm_CryBb_weAeUIsjje2vu7uCNh9UNLsqIQtDCbKWTMI8FIbJBrKTfEryVu4f_98YNZeP3Qq8e9-w4QqgpgSZCd2eyHTGTUUdItK_5Q_qEqpp9UlKFiZgQ6kwAAmI0J6DJGPypGaqevm11vUUPhyW-U053bBbsIwib_0f3r9pGrpDxBDQmUvzkZu1GuDNhWwvs5xt2X24TLwmoT8BD59RG3LBWvtIQSdd2WQSWdva1q3r6uDnL3cp0NyKp2sYgXU2yr-OXQ'
```
As result:
```
403 Forbidden:
Game with id101 is not allowed for platform with id: 0af4aced-2386-4f92-91b7-71cf6d070eec
```
## Accessing from platform which is blocked to any game
1. Requesting token from demo-external-jwt-issuer:
```
curl --location 'localhost:8081/api/platform' \
--header 'Content-Type: application/json' \
--data '{
    "playerId": "c8185ca2-9975-4ef6-bc5e-83c3ec3b4f3c",
    "platformId": "20652211-babd-4994-b3b7-101da062d463"
}'
```
As result:
```json
eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiIyMDY1MjIxMS1iYWJkLTQ5OTQtYjNiNy0xMDFkYTA2MmQ0NjMiLCJzdWIiOiJjODE4NWNhMi05OTc1LTRlZjYtYmM1ZS04M2MzZWMzYjRmM2MiLCJncm91cHMiOlsiVVNFUiJdLCJleHAiOjE3NjY2ODIwMjAsImlhdCI6MTc2NjY3ODQyMCwianRpIjoiYWI1OGJjMjMtNWU5NS00NWM4LWFkYjktODY4MGNlYmMyYjU2In0.ffrwjdPh0vSL5gIl-HgfGWw3wuXXV_UZEsbZv0UfDOg27k2rG4dMc2ooRDdjH6-z3KubxfLrg6VvYrcDbuGfSCmrZK0ZrfrqYrEYRAOAOjjWJ-DqEl4Uj3ezTE1iHaHTeDPVMgXYGzQRKk9njQd2zTU3VfUaDkoW19CQ9Ek4JZKOl9m5n6W1rmS6Su89sIq-5lzmO_5NoG6p9dOsesQJ7Ob1sFQkA1dHP55aAx_JSeLu7EuYuoucWW_Ft7PPt2TpJ-WCWRVUlGXbwEGPXVffILH34tcUtrTaqsXx7yz90x3FA80m0K8JikKL68jiKp5iyF0jSpx-0Oj5WwlLj_jicg
```
2. Launch any game (should not allow because platform is INACTIVE):
```
curl --location --request POST 'localhost:8080/api/v1/game/100/launch?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiIyMDY1MjIxMS1iYWJkLTQ5OTQtYjNiNy0xMDFkYTA2MmQ0NjMiLCJzdWIiOiJjODE4NWNhMi05OTc1LTRlZjYtYmM1ZS04M2MzZWMzYjRmM2MiLCJncm91cHMiOlsiVVNFUiJdLCJleHAiOjE3NjY2ODIwMjAsImlhdCI6MTc2NjY3ODQyMCwianRpIjoiYWI1OGJjMjMtNWU5NS00NWM4LWFkYjktODY4MGNlYmMyYjU2In0.ffrwjdPh0vSL5gIl-HgfGWw3wuXXV_UZEsbZv0UfDOg27k2rG4dMc2ooRDdjH6-z3KubxfLrg6VvYrcDbuGfSCmrZK0ZrfrqYrEYRAOAOjjWJ-DqEl4Uj3ezTE1iHaHTeDPVMgXYGzQRKk9njQd2zTU3VfUaDkoW19CQ9Ek4JZKOl9m5n6W1rmS6Su89sIq-5lzmO_5NoG6p9dOsesQJ7Ob1sFQkA1dHP55aAx_JSeLu7EuYuoucWW_Ft7PPt2TpJ-WCWRVUlGXbwEGPXVffILH34tcUtrTaqsXx7yz90x3FA80m0K8JikKL68jiKp5iyF0jSpx-0Oj5WwlLj_jicg'
```
As the result:
```
Error 401:
Platform with id 20652211-babd-4994-b3b7-101da062d463 is blocked
```

## Accessing to play logic without internal token
Making request without internal token will return 401 error by default