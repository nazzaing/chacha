package com.chacha.post.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostCreate {

    public String title;
    public String content;

    @Override
    public String toString() {
        return "PostCreate{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
