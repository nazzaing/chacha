package com.chacha.post.service;

import com.chacha.post.domain.Post;
import com.chacha.post.dto.PostCreate;
import com.chacha.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void write(PostCreate postCreate) {

        Post post = new Post(postCreate.getTitle(), postCreate.getContent());
        postRepository.save(post);
        // repository.save(postCreate)
    }
}
