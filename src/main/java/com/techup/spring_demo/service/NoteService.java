package com.techup.spring_demo.service;

import com.techup.spring_demo.dto.NoteRequest;
import com.techup.spring_demo.dto.NoteResponse;
import com.techup.spring_demo.entity.Note;
import com.techup.spring_demo.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public List<NoteResponse> getAll() {
        return noteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public NoteResponse create(NoteRequest request) {
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        Note savedNote = noteRepository.save(note);
        return toResponse(savedNote);
    }

    public NoteResponse update(Long id, NoteRequest request) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        Note updatedNote = noteRepository.save(note);
        return toResponse(updatedNote);
    }

    public void delete(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        noteRepository.delete(note);
    }

    private NoteResponse toResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .build();
    }
}
