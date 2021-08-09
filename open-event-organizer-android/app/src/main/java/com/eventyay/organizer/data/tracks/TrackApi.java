package com.eventyay.organizer.data.tracks;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TrackApi {

    @GET("events/{id}/tracks?include=event&fields[event]=id&page[size]=0")
    Observable<List<Track>> getTracks(@Path("id") long id);

    @POST("tracks")
    Observable<Track> postTrack(@Body Track track);

    @GET("tracks/{track_id}")
    Observable<Track> getTrack(@Path("track_id") long id);

    @PATCH("tracks/{track_id}")
    Observable<Track> updateTrack(@Path("track_id") long id, @Body Track track);

    @DELETE("tracks/{track_id}")
    Completable deleteTrack(@Path("track_id") long id);
}
