
object SpotifyPlaylistAnalysis {

  //send post request following https://developer.spotify.com/documentation/web-api/tutorials/getting-started#request-an-access-token to get the token
  //this token will expire in 3600s, we have to regenerate token once it is expired
  val token = "BQC4WpvHeZ0vuzhbnHVUO6AwqozrpYi7AaY9SlG4IVHE0wKb_xsntyGGUE5q1uCKmqv7xwQ6_OJeiC4d5sx-MldveTCsMbeOm5KVnIn7kVyu6GrgAW3wJn-8ptoH9FBkva1LmS5SCMw"

  val playlistEndpoint = "https://api.spotify.com/v1/playlists/"
  val artistEndpoint = "https://api.spotify.com/v1/artists/"

  def main(args: Array[String]): Unit = {
    val playlistId = "5Rrf7mqN8uus2AaQQQNdc1"
    // Get playlist data in JSON format
    val playlistUrl = s"$playlistEndpoint$playlistId"
    val playlistResponse = requests.get(playlistUrl,
      headers = Map("Authorization" -> s"Bearer $token")
    )

    val playlistJson = ujson.read(playlistResponse.text)

    // Spotify playlist JSON structure:
    // playlistJson("tracks")("items") is an array where each element contains a "track" object.
    // Each track object has fields "duration_ms", "name" and "artists" (an array)
    val tracks = playlistJson("tracks")("items").arr.flatMap { item =>
      val track = item("track")
      // some tracks may be null, filter them out
      if (track == null) None
      else {
        // extract song details
        val songName = track("name").str
        val duration = track("duration_ms").num.toLong
        //each artist object has an "id" and "name"
        val artists = track("artists").arr.map { artist =>
          (artist("name").str, artist("id").str)
        }
        Some((songName, duration, artists))
      }
    }

    // Get the top 10 longest songs
    val top10Songs = tracks.sortBy(-_._2).take(10)

    println("Part 1) Top 10 Longest Songs (Song name, duration_ms):")
    top10Songs.foreach { case (name, duration, _) =>
      println(s"$name , $duration")
    }

    // Collect all unique artists from these songs
    val artistMap = scala.collection.mutable.Map[String, String]() // Map artistId -> artistName
    top10Songs.foreach { case (_, _, artists) =>
      artists.foreach { case (name, id) =>
        artistMap.getOrElseUpdate(id, name)
      }
    }

    // For each artist, call the artist endpoint to get follower count
    val artistDetails = artistMap.toSeq.flatMap { case (artistId, name) =>
      try {
        val artistUrl = s"$artistEndpoint$artistId"
        val artistResponse = requests.get(artistUrl,
          headers = Map("Authorization" -> s"Bearer $token")
        )
        val artistJson = ujson.read(artistResponse.text)
        // Followers are in the "followers" field under "total"
        val followers = artistJson("followers")("total").num.toLong
        Some((name, followers))
      } catch {
        case e: Exception =>
          println(s"Failed to fetch details for artist: $name ($artistId). Reason: ${e.getMessage}")
          None
      }
    }

    // Sort the artists by follower count in descending order
    val sortedArtists = artistDetails.sortBy(-_._2)

    println("\nPart 2) Artists (sorted by follower count):")
    sortedArtists.foreach { case (name, followers) =>
      println(s"$name : $followers")
    }
  }
}
