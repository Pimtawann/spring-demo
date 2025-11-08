package com.techup.spring_demo.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NoteResponse {
  Long id;
  String title;
  String content;
}
