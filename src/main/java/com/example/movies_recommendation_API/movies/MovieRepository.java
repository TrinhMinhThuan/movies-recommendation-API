package com.example.movies_recommendation_API.movies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository  extends MongoRepository<Movie, Long> {

    @Query("{ 'tmdb_id': ?0 }")
    Movie findOneByTmdb_id(@Param("tmdb_id") int tmdb_id);

    List<Movie> findListByTitleContainingIgnoreCase(String keyword);

    @Query("{ 'credits.cast.name': { $regex: ?0, $options: 'i' } }")
    List<Movie> findListByCastName(String castName);

    @Query("{ '$and': [ { 'title': { '$regex': ?0, '$options': 'i' } }, { 'credits.cast.name': { '$regex': ?1, '$options': 'i' } } ] }")
    Page<Movie> findListByTitleAndCastName(String title, String castName, Pageable pageable);

    @Query("{'genres.name': { $all: ?0 }, 'vote_average': { $gte: ?1, $lte: ?2 }, 'release_date': { $gte: ?3, $lte: ?4 }}")
    Page<Movie> filterMovies(List<String> genres,
                             Double minVoteAverage, Double maxVoteAverage,
                             String startDate, String endDate,
                             Pageable pageable);


}

