package com.chacha.post.repository;

import com.chacha.post.domain.Post;
import com.chacha.post.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
