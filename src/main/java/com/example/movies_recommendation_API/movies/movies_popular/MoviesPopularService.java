package com.example.movies_recommendation_API.movies.movies_popular;


import com.example.movies_recommendation_API.response.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MoviesPopularService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ResponseEntity<?> getAllMovies(
            Integer pageNumber, Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Tạo Query với phân trang
        Query query = new Query().with(pageable);

        // Tổng số phần tử trong collection
        long totalElements = mongoTemplate.count(new Query(), "movies_popular");

        // Dữ liệu phân trang
        List<Map> movies = mongoTemplate.find(query, Map.class, "movies_popular");

        // Tạo PageImpl để trả về thông tin phân trang chi tiết
        PageImpl<Map> moviePage = new PageImpl<>(movies, pageable, totalElements);

        // Trả về dữ liệu kèm thông tin phân trang
        return ResponseEntity.ok().body(
                new ResponseSuccess(moviePage)
        );
    }
}
