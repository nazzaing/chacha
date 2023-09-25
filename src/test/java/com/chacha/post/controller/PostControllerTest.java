package com.chacha.post.controller;

import com.chacha.post.domain.Post;
import com.chacha.post.dto.PostCreate;
import com.chacha.post.repository.PostRepository;
import com.chacha.post.request.PostEdit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    // 각 테스트 수행 전 실행
    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 요청시 Hello World를 출력한다ㅑ.")
    void test() throws Exception {
        // given
        //PostCreate request = new PostCreate("제목입니다.", "내용입니다.");
        PostCreate request =  PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // 객체를 json 형태로 읽을수 있게 만드는 것
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("/posts 요청시 title값은 필수다.")
    void test2() throws Exception {
        // given
        //PostCreate request = new PostCreate("제목입니다.", "내용입니다.");
        PostCreate request =  PostCreate.builder()
                .title("")
                .content("내용입니다.")
                .build();

        // 객체를 json 형태로 읽을수 있게 만드는 것
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validation.title").value("타이틀을 입력해주세요."))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    void test3() throws Exception {
        // given
        //PostCreate request = new PostCreate("제목입니다.", "내용입니다.");
        PostCreate request =  PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // 객체를 json 형태로 읽을수 있게 만드는 것
        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // then
        Assertions.assertEquals(1L, postRepository.count());

        Post post = postRepository.findAll().get(0);
        Assertions.assertEquals("제목입니다.", post.getTitle());
        Assertions.assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test4() throws Exception {
        // given
        Post post =  Post.builder()
                .title("123456789012345")
                .content("bar")
                .build();
        postRepository.save(post);

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(post.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("1234567890"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("bar"))
                .andDo(MockMvcResultHandlers.print());

        // then
    }

    @Test
    @DisplayName("글 여러개 조회")
    void test5() throws Exception {
        // given
        /*Post post1 =  Post.builder()
                .title("title1")
                .content("content1")
                .build();*/
//        postRepository.save(post1);
        /*postRepository.saveAll(List.of(
                Post.builder()
                        .title("title1")
                        .content("content1")
                        .build(),
                Post.builder()
                        .title("title2")
                        .content("content2")
                        .build()
        ));*/
        List<Post> requestPosts = IntStream.range(1, 31)
                .mapToObj(i -> Post.builder()
                        .title("boo 제목 - " + i)
                        .content("boo 내용 - " + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(requestPosts);

        /*Post post2 =  Post.builder()
                .title("title2")
                .content("content2")
                .build();
        postRepository.save(post2);*/

        // when
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?page=0&sort=id,desc&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.is(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("boo 제목 - 30"))
                /*.andExpect(MockMvcResultMatchers.jsonPath("$[0].content").value("content1"))*/
                .andExpect(MockMvcResultMatchers.jsonPath("$[4].title").value("boo 제목 - 26"))
                /*.andExpect(MockMvcResultMatchers.jsonPath("$[1].content").value("content2"))*/
                .andDo(MockMvcResultHandlers.print());

        // then
    }

    @Test
    @DisplayName("글 제목 수정")
    void test6() throws Exception {
        //given
        Post post = Post.builder()
                .title("boo 제목")
                .content("boo 내용")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("호호호호")
                .content("boo 내용")
                .build();

        // when
        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // then
    }
}