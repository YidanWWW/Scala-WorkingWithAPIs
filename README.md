Requirement:

Use Scala requests to get the Spotify Playlist data in JSON format.

The ID of the playlist is : 5Rrf7mqN8uus2AaQQQNdc1 

The Spotify developer API is : https://developer.spotify.com/console/get-playlist/Links to an external site.

Playlist : https://open.spotify.com/playlist/5Rrf7mqN8uus2AaQQQNdc1Links to an external site. 

 

In the JSON response, you will have the duration_ms field. This is the duration of each song in milliseconds. You are to find the top 10 longest songs. Each song will have an Artist name and an artist ID. There may be multiple artists for the same song. You are to query the artist details of every artist in the top 10 longest songs and display them in order of their followers.

The Spotify API to query the artist is : https://developer.spotify.com/console/get-artist/Links to an external site.

You must call this API for every artist ID in the top 10 longest songs.

 

The expected output would be : 

 

Part 1)

Songname1 , duration_ms

Songname2 , duration_ms

……

Songname10 , duration_ms

 

 

Part 2)

Artist1 : follower_count

Artist2 : follower_count … etc

 

All code must be in Scala.
