
object SpotifyPlaylistAnalysis {

  //Get this token by following https://developer.spotify.com/documentation/web-api/concepts/access-token
  //once this token is expired, we have to send post request to get a new one.
  val token = "BQC4WpvHeZ0vuzhbnHVUO6AwqozrpYi7AaY9SlG4IVHE0wKb_xsntyGGUE5q1uCKmqv7xwQ6_OJeiC4d5sx-MldveTCsMbeOm5KVnIn7kVyu6GrgAW3wJn-8ptoH9FBkva1LmS5SCMw"

  val playlistEndpoint = "https://api.spotify.com/v1/playlists"
  val artistEndpoint = "https://api.spotify.com/v1/artists/"

  // paginate to retrieve all songs from the playlist.
  def fetchAllTracks(playlistId: String): Seq[ujson.Value] = {
    var allTracks = Seq[ujson.Value]()
    var offset = 0
    val limit = 100
    var hasMore = true

    while (hasMore) {
      val playlistUrl = s"$playlistEndpoint/$playlistId/tracks?limit=$limit&offset=$offset"
      val playlistResponse = requests.get(
        playlistUrl,
        headers = Map("Authorization" -> s"Bearer $token")
      )

      if (playlistResponse.statusCode == 401) {
        println("Token is invalid")
        sys.exit(1)
      }

      val playlistJson = ujson.read(playlistResponse.text)
      val items = playlistJson("items").arr
      allTracks ++= items
      offset += items.length
      hasMore = items.length == limit
    }
    allTracks
  }

  def main(args: Array[String]): Unit = {
    val playlistId = "5Rrf7mqN8uus2AaQQQNdc1"
    val allTracks = fetchAllTracks(playlistId)

    val tracks = allTracks.flatMap { item =>
      val track = item("track")
      if (track == null) None
      else {
        val songName = track("name").str
        val duration = track("duration_ms").num.toLong
        val artists = track("artists").arr.map { artist =>
          (artist("name").str, artist("id").str)
        }
        Some((songName, duration, artists))
      }
    }

    // top 10 songs
    val top10Songs = tracks.sortBy(-_._2).take(10)

    println("Part 1) Top 10 Longest Songs (Song name, duration_ms):")
    top10Songs.foreach { case (name, duration, _) =>
      println(s"$name , $duration")
    }

    val artistMap = scala.collection.mutable.Map[String, String]()
    top10Songs.foreach { case (_, _, artists) =>
      artists.foreach { case (name, id) =>
        artistMap.getOrElseUpdate(id, name)
      }
    }

    val artistDetails = artistMap.toSeq.flatMap { case (artistId, name) =>
      try {
        val artistUrl = s"$artistEndpoint$artistId"
        val artistResponse = requests.get(
          artistUrl,
          headers = Map("Authorization" -> s"Bearer $token")
        )
        if (artistResponse.statusCode == 401) {
          println(s"Get artist $name but token is invalid")
          sys.exit(1)
        }
        val artistJson = ujson.read(artistResponse.text)
        val followers = artistJson("followers")("total").num.toLong
        Some((name, followers))
      } catch {
        case e: Exception =>
          println(s"Get artist $name ($artistId) Information Failure: ${e.getMessage}")
          None
      }
    }

    val sortedArtists = artistDetails.sortBy(-_._2)
    println("\nPart 2) Artists (sorted by follower count):")
    sortedArtists.foreach { case (name, followers) =>
      println(s"$name : $followers")
    }
  }
}
