# java-filmorate
Template repository for Filmorate project.

![](../java-filmorate/QuickDBD-export.png)

### Main sql-queries:

#### UserController methods:
findAll 
- SELECT * FROM user;

findUserById(Long id) 
- SELECT * FROM users WHERE user_id = ?;

findAllFriends(Long id) 
- SELECT * FROM user_friends WHERE user_id = ?

findCommonFriends(Long id, Long otherId) 
- SELECT friend_id FROM (SELECT * FROM user_friends AS a WHERE user_id = ?
OUTER JOIN user_friends AS b ON a.friend_id = b.friend_id WHERE 
user_id = ?;

#### FilmController methods:
findAll
- SELECT * FROM films

findFilmById(Long id)
- SELECT * FROM films WHERE film_id = ?;

getMostPopularFilms(Integer count)
- SELECT film_id, COUNT(user_id) FROM likes GROUP BY film_id ORDER BY
  COUNT(user_id) DESC LIMIT ?;

![img.png](img.png)
