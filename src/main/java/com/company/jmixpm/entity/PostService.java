package com.company.jmixpm.entity;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PostService {

    public Post[] fetchPosts() {
        RestTemplate rest = new RestTemplate();
        return rest.getForObject("https://jsonplaceholder.typicode.com/posts",
                Post[].class);
    }

    public int postsTotalCount() {
        return fetchPosts().length;
    }

    public Post[] fetchPosts(int firstResult, int maxResult) {
        RestTemplate rest = new RestTemplate();
        return rest.getForObject("https://jsonplaceholder.typicode" +
                        ".com/posts?_start={firstResult}&_end={maxResult}",
                Post[].class, firstResult, firstResult + maxResult);
    }

    public UserInfo fetchUserInfo(Long id) {
        RestTemplate rest = new RestTemplate();
        return rest.getForObject("https://jsonplaceholder.typicode" +
                ".com/users/{id}", UserInfo.class, id);
    }
}